import { GridColDef } from "@mui/x-data-grid";
import "./add.scss";
import React, { useState } from "react";
import { API_ENDPOINTS } from "../../api/endpoints";
import { useAuth } from "../../context/AuthContext";
import { useNavigate } from "react-router-dom";

type Props = {
    slug: string;
    columns: GridColDef[];
    setOpen: React.Dispatch<React.SetStateAction<boolean>>;
};

const AddExam = ({ slug, columns, setOpen }: Props) => {
    const [formData, setFormData] = useState<any>({});
    const [error, setError] = useState<string | null>(null);

    const { user, logout } = useAuth();
    const token = user?.token;
    const navigate = useNavigate();

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        setError(null);

        const requiredFields = [
            "name",
            "date",
            "english_min",
            "english_max",
            "math_min",
            "math_max",
        ];

        for (const field of requiredFields) {
            if (!formData[field]) {
                setError("Пожалуйста, заполните все обязательные поля.");
                return;
            }
        }

        try {
            const response = await fetch(API_ENDPOINTS.EXAMS, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify(formData),
            });

            if (!response.ok) {
                if (response.status === 401) {
                    logout();
                    navigate("/login");
                }

                const text = await response.text();
                throw new Error(text || "Ошибка при отправке данных");
            }

            const result = await response.json();
            console.log("Экзамен успешно создан:", result);
            setOpen(false);
        } catch (err: any) {
            console.error("Ошибка:", err);
            setError(`Ошибка: ${err.message}`);
        }
    };

    return (
        <div className="add">
            <div className="modal">
                <h1>Добавить {slug}</h1>
                <span className="close" onClick={() => setOpen(false)}>X</span>
                <form onSubmit={handleSubmit}>
                    {columns
                        .filter((item) => item.field !== "id" && item.field !== "created_at")
                        .map((column) => (
                            <div className="item" key={column.field}>
                                <label>{column.headerName}</label>
                                <input
                                    type={
                                        column.field === "date"
                                            ? "date"
                                            : column.type === "number"
                                                ? "number"
                                                : "text"
                                    }
                                    placeholder={column.headerName}
                                    required
                                    onChange={(e) =>
                                        setFormData((prev: any) => ({
                                            ...prev,
                                            [column.field]:
                                                column.type === "number"
                                                    ? parseFloat(e.target.value)
                                                    : e.target.value,
                                        }))
                                    }
                                />
                            </div>
                        ))}

                    {error && <p className="error">{error}</p>}

                    <button type="submit">Создать</button>
                </form>
            </div>
        </div>
    );
};

export default AddExam;

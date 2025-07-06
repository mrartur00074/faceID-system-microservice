import { GridColDef } from "@mui/x-data-grid";
import "./add.scss";
import React, { useState } from "react";
import { API_ENDPOINTS } from "../../api/endpoints";
import { useAuth } from "../../context/AuthContext";
import {useNavigate} from "react-router-dom";

type Props = {
  slug: string;
  columns: GridColDef[];
  setOpen: React.Dispatch<React.SetStateAction<boolean>>;
};

const Add = (props: Props) => {
  const [formData, setFormData] = useState<any>({});
  const [photoBase64, setPhotoBase64] = useState<String | null>(null);
  const [error, setError] = useState<string | null>(null);

  const { user, logout } = useAuth();
  const token = user?.token;
  const navigate = useNavigate();

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onloadend = () => {
        const base64String = (reader.result as string).split(",")[1]; // только base64 без префикса
        setPhotoBase64(base64String);
      };
      reader.readAsDataURL(file);
    }
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setError(null);

    if (!formData.applicantId || !photoBase64) {
      setError("Поля ID и Фото обязательны.");
      return;
    }

    const payload = {
      applicantId: formData.applicantId,
      base64: photoBase64,
    };

    try {
      const response = await fetch(API_ENDPOINTS.ADD_APPLICANT, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(payload),
      });


      if (!response.ok) {
        if (response.status === 401) {
          logout();               // ✅ выходим из системы
          navigate("/login");     // ✅ перекидываем на логин
        }

        const text = await response.text();
        throw new Error(text || "Ошибка при отправке данных");
      }

      const result = await response.json();
      console.log("Успешно отправлено:", result);
      props.setOpen(false);
    } catch (err: any) {
      console.error("Ошибка:", err);
      setError(`Ошибка: ${err.message}`);
    }
  };

  return (
      <div className="add">
        <div className="modal">
          <h1>Добавить {props.slug}</h1>
          <span className="close" onClick={() => props.setOpen(false)}>
          X
        </span>
          <form onSubmit={handleSubmit}>
            {props.columns
                .filter((item) => item.field !== "img")
                .map((column) => (
                    <div className="item" key={column.field}>
                      <label>{column.headerName}</label>

                      {column.field === "base64" ? (
                          <input
                              type="file"
                              accept="image/*"
                              onChange={handleFileChange}
                              required
                          />
                      ) : (
                          <input
                              type={column.type === "number" ? "number" : "text"}
                              placeholder={column.field}
                              required={column.field === "applicantId"}
                              onChange={(e) =>
                                  setFormData((prev: any) => ({
                                    ...prev,
                                    [column.field]: e.target.value,
                                  }))
                              }
                          />
                      )}
                    </div>
                ))}

            {error && <p className="error">{error}</p>}

            <button type="submit">Отправить</button>
          </form>
        </div>
      </div>
  );
};

export default Add;
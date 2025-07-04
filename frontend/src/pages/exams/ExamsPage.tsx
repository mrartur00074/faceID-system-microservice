import { GridColDef } from "@mui/x-data-grid";
import DataTable from "../../components/dataTable/DataTable";
import { useQuery } from "@tanstack/react-query";
import { useAuth } from "../../context/AuthContext";
import axios from "axios";
import { API_ENDPOINTS } from "../../api/endpoints";
import { useNavigate } from "react-router-dom";
import {useState} from "react";
import AddExam from "../../components/add/AddExam.tsx";

export const columns: GridColDef[] = [
    {
        field: "id",
        headerName: "ID",
        width: 90,
    },
    {
        field: "name",
        headerName: "Название",
        width: 200,
    },
    { field: "date", headerName: "Дата", type: "date", flex: 1 },
    { field: "english_min", headerName: "Мин. балл по английскому", type: "number", flex: 1 },
    { field: "english_max", headerName: "Макс. балл по английскому", type: "number", flex: 1 },
    { field: "math_min", headerName: "Мин. балл по математике", type: "number", flex: 1 },
    { field: "math_max", headerName: "Макс. балл по математике", type: "number", flex: 1 }
];

const ExamsPage = () => {
    const { user, logout } = useAuth();
    const token = user?.token;
    const navigate = useNavigate();
    const [open, setOpen] = useState(false);

    const { isLoading, data, error } = useQuery({
        queryKey: ["exams"],
        queryFn: async () => {
            try {
                const { data } = await axios.get(API_ENDPOINTS.EXAMS, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });

                const exams = data.map((exam: any) => ({
                    ...exam,
                    date: exam.date ? new Date(exam.date) : null,
                }));

                return exams;
            } catch (err: any) {
                if (err.response?.status === 401) {
                    logout();
                    navigate("/login");
                }
                throw err;
            }
        },
        enabled: !!token,
    });

    return (
        <div className="users">
            <div className="info">
                <h1>Экзамены</h1>
                <button onClick={() => setOpen(true)}>Добавить экзамен</button>
            </div>

            {isLoading ? (
                "Загрузка..."
            ) : error ? (
                <div>Ошибка при загрузке экзаменов</div>
            ) : (
                <DataTable slug="exams" columns={columns} rows={data} errors={true} blacklist={false} />
            )}

            {open && <AddExam slug="exam" columns={columns} setOpen={setOpen} />}
        </div>
    );
};

export default ExamsPage;
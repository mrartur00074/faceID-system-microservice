import { GridColDef } from "@mui/x-data-grid";
import DataTable from "../../components/dataTable/DataTable";
import "./users.scss";
import { useState } from "react";
import Add from "../../components/add/Add";
import { useQuery } from "@tanstack/react-query";
import { useAuth } from "../../context/AuthContext";
import axios from "axios";
import { API_ENDPOINTS } from "../../api/endpoints";
import { useNavigate } from "react-router-dom";

const getImageMimeType = (base64: string): string => {
  if (base64.startsWith("/9j/")) return "jpeg";       // JPEG
  if (base64.startsWith("iVBOR")) return "png";        // PNG
  if (base64.startsWith("UklGR")) return "webp";       // WEBP
  return "jpeg"; // fallback
};

export const columns: GridColDef[] = [
  {
    field: "applicant_id",
    headerName: "ID",
    width: 90,
    type: 'number',
    valueGetter: (params) => params.row.applicant_id?.toString(),
  },
  {
    field: "base64",
    headerName: "Фото",
    width: 100,
    renderCell: (params) => {
      if (!params.value) return "Нет фото";
      const mimeType = getImageMimeType(params.value);
      const imageSrc = `data:image/${mimeType};base64,${params.value}`;
      return (
          <img
              src={imageSrc}
              alt="Фото"
              style={{ width: 40, height: 40, objectFit: "cover", borderRadius: "50%" }}
          />
      );
    },
  },
  {
    field: "name",
    headerName: "Имя",
    width: 150,
  },
  {
    field: "surname",
    headerName: "Фамилия",
    width: 150,
  },
  {
    field: "phone_num",
    headerName: "Телефон",
    width: 150,
  },
  {
    field: "school",
    headerName: "Школа",
    width: 200,
  },
  {
    field: "attempt",
    headerName: "Попытка",
    width: 100,
    type: "number",
  },
  {
    field: "status",
    headerName: "Статус",
    width: 150,
  },
  {
    field: "created_at",
    headerName: "Создано",
    width: 180,
    valueGetter: (params) =>
        new Date(params.value).toLocaleString("ru-RU", {
          year: "numeric",
          month: "short",
          day: "numeric",
          hour: "2-digit",
          minute: "2-digit",
        }),
  },
];

const Users = () => {
  const [open, setOpen] = useState(false);
  const { user, logout } = useAuth();
  const token = user?.token;
  const navigate = useNavigate();

  const { isLoading, data, error } = useQuery({
    queryKey: ["students"],
    queryFn: async () => {
      try {
        const res = await axios.get(API_ENDPOINTS.GET_STUDENTS, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        return res.data;
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
          <h1>Students</h1>
          <button onClick={() => setOpen(true)}>Add New Student</button>
        </div>

        {isLoading ? (
            "Loading..."
        ) : error ? (
            <div>Error fetching data</div>
        ) : (
            <DataTable slug="users" columns={columns} rows={data} errors={false} blacklist={false}/>
        )}

        {open && <Add slug="user" columns={columns} setOpen={setOpen} />}
      </div>
  );
};

export default Users;
import { GridColDef } from "@mui/x-data-grid";
import DataTable from "../../components/dataTable/DataTable";
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
    field: "error",
    headerName: "Ошибка",
    width: 500,
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

const ErrorStudents = () => {
  const { user, logout } = useAuth();
  const token = user?.token;
  const navigate = useNavigate();

  const { isLoading, data, error } = useQuery({
    queryKey: ["ban-students"],
    queryFn: async () => {
      try {
        const res = await axios.get(API_ENDPOINTS.GET_BAN_STUDENTS, {
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
          <h1>Banned Students</h1>
        </div>

        {isLoading ? (
            "Loading..."
        ) : error ? (
            <div>Error fetching data</div>
        ) : (
            <DataTable slug="blacklist" columns={columns} rows={data} errors={false} blacklist={true}/>
        )}
      </div>
  );
};

export default ErrorStudents;
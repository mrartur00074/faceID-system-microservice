import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios, { AxiosError } from "axios"; // Импортируем AxiosError
import { useAuth } from "../../context/AuthContext";
import { API_ENDPOINTS } from "../../api/endpoints";
import "./user.scss";

// Тип для данных студента
interface UserData {
  id: number;
  applicant_id: number;
  name: string | null;
  surname: string | null;
  phone_num: string | null;
  school: string | null;
  attempt: number;
  status: string | null;
  base64: string;
  created_at: string;
}

const User = () => {
  const { id } = useParams<{ id: string }>();
  const userId = Number(id);
  const navigate = useNavigate();
  const { user, logout } = useAuth();  // Получаем user и logout из контекста
  const token = user?.token

  const [userData, setUserData] = useState<UserData | null>(null);
  const [isEditing, setIsEditing] = useState(false);
  const [formData, setFormData] = useState<UserData | null>(null);

  useEffect(() => {
    if (!user) {
      navigate("/login"); // Если нет токена, перенаправляем на логин
      return;
    }

    const fetchUser = async () => {
      try {
        const response = await axios.get(`${API_ENDPOINTS.GET_APPLICANT}/${userId}/`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        setUserData(response.data);
        setFormData(response.data); // Инициализируем данные формы
      } catch (error) {
        if (axios.isAxiosError(error)) {
          const axiosError = error as AxiosError;
          if (axiosError.response && axiosError.response.status === 401) {
            logout();
            navigate("/login");
          } else {
            console.error("Error fetching user data", axiosError);
          }
        } else {
          console.error("Unexpected error:", error);
        }
      }
    };

    fetchUser();
  }, [user, userId, logout, navigate]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (formData) {
      setFormData({
        ...formData,
        [e.target.name]: e.target.value,
      });
    }
  };

  const handleSave = async () => {
    if (formData) {
      try {
        await axios.put(`${API_ENDPOINTS.GET_APPLICANT}/${userId}/`, formData, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        setIsEditing(false);
        setUserData(formData); // Обновляем состояние пользователя
      } catch (error) {
        if (axios.isAxiosError(error)) {
          const axiosError = error as AxiosError;
          if (axiosError.response && axiosError.response.status === 401) {
            logout();
            navigate("/login");
          } else {
            console.error("Error updating user data", axiosError);
          }
        } else {
          console.error("Unexpected error:", error);
        }
      }
    }
  };

  if (!userData) {
    return <div>Loading...</div>;
  }

  return (
      <div className="user">
        <h1>User Details</h1>
        <div className="userInfo">
          <img
              src={`data:image/jpeg;base64,${userData.base64}`} // Преобразуем base64 в изображение
              alt={`${userData.name || "No Name"} ${userData.surname || "No Surname"}`}
              className="userAvatar"
          />
          <div className="userDetails">
            <p><strong>Name:</strong> {isEditing ? <input type="text" name="name" value={formData?.name || ''} onChange={handleChange} /> : userData.name}</p>
            <p><strong>Surname:</strong> {isEditing ? <input type="text" name="surname" value={formData?.surname || ''} onChange={handleChange} /> : userData.surname}</p>
            <p><strong>Phone Number:</strong> {isEditing ? <input type="text" name="phone_num" value={formData?.phone_num || ''} onChange={handleChange} /> : userData.phone_num}</p>
            <p><strong>School:</strong> {isEditing ? <input type="text" name="school" value={formData?.school || ''} onChange={handleChange} /> : userData.school}</p>
            <p><strong>Attempt:</strong> {isEditing ? <input type="number" name="attempt" value={formData?.attempt || 0} onChange={handleChange} /> : userData.attempt}</p>
            <p><strong>Status:</strong> {isEditing ? <input type="text" name="status" value={formData?.status || ''} onChange={handleChange} /> : userData.status}</p>
            <p><strong>Created At:</strong> {new Date(userData.created_at).toLocaleString()}</p>
          </div>
        </div>

        <div className="userActions">
          {isEditing ? (
              <>
                <button onClick={handleSave}>Save Changes</button>
                <button onClick={() => setIsEditing(false)}>Cancel</button>
              </>
          ) : (
              <button onClick={() => setIsEditing(true)}>Edit</button>
          )}
        </div>
      </div>
  );
};

export default User;
import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";
import { useAuth } from "../../context/AuthContext";
import { API_ENDPOINTS } from "../../api/endpoints";

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

const BannedStudent = () => {
  const { id } = useParams<{ id: string }>();
  const userId = Number(id);
  const navigate = useNavigate();
  const { user, logout } = useAuth();
  const token = user?.token;

  const [userData, setUserData] = useState<UserData | null>(null);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  useEffect(() => {
    if (!user) {
      navigate("/login");
      return;
    }

    const fetchUser = async () => {
      try {
        const response = await axios.get(`${API_ENDPOINTS.GET_BAN_STUDENT}/${userId}/`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        setUserData(response.data);
      } catch (error) {
        if (axios.isAxiosError(error) && error.response?.status === 401) {
          logout();
          navigate("/login");
        } else {
          setErrorMessage("Ошибка при загрузке данных.");
        }
      }
    };

    fetchUser();
  }, [user, userId, logout, navigate]);

  const handleRestore = async () => {
    try {
      await axios.post(`${API_ENDPOINTS.RESTORE_BLACKLIST}/${userData?.applicant_id}/restore/`, null, {
        headers: { Authorization: `Bearer ${token}` },
      });
      navigate("/blacklist"); // Перенаправляем после восстановления
    } catch (error) {
      setErrorMessage("Ошибка при восстановлении из бана.");
    }
  };

  if (!userData) return <div>Loading...</div>;

  return (
      <div className="user">
        <h1>Banned Student</h1>
        <div className="userInfo">
          <img
              src={`data:image/jpeg;base64,${userData.base64}`}
              alt={`${userData.name || "No Name"} ${userData.surname || "No Surname"}`}
              className="userAvatar"
          />
          <div className="userDetails">
            <p><strong>Applicant ID:</strong> {userData.applicant_id}</p>
            <p><strong>Name:</strong> {userData.name}</p>
            <p><strong>Surname:</strong> {userData.surname}</p>
            <p><strong>Phone Number:</strong> {userData.phone_num}</p>
            <p><strong>School:</strong> {userData.school}</p>
            <p><strong>Attempt:</strong> {userData.attempt}</p>
            <p><strong>Status:</strong> {userData.status}</p>
            <p><strong>Created At:</strong> {new Date(userData.created_at).toLocaleString()}</p>
          </div>
        </div>

        <div className="userActions">
          <button onClick={handleRestore}>Убрать из бана</button>
          {errorMessage && <p style={{ color: "red" }}>{errorMessage}</p>}
        </div>
      </div>
  );
};

export default BannedStudent;
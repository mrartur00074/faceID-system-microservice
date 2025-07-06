import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";
import { useAuth } from "../../context/AuthContext";
import { API_ENDPOINTS } from "../../api/endpoints";

interface UserData {
  id: number;
  applicantId: number;
  name: string | null;
  surname: string | null;
  phoneNum: string | null;
  school: string | null;
  attempt: number;
  status: string | null;
  base64: string;
  created_at: string;
}

const BACKEND_URL = import.meta.env.VITE_BACKEND_URL;

const ErrorStudent = () => {
  const { id } = useParams<{ id: string }>();
  const userId = Number(id);
  const navigate = useNavigate();
  const { user, logout } = useAuth();
  const token = user?.token;

  const [userData, setUserData] = useState<UserData | null>(null);
  const [isEditing, setIsEditing] = useState(false);
  const [formData, setFormData] = useState<UserData | null>(null);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  useEffect(() => {
    /*if (!user) {
      navigate("/login");
      return;
    }*/

    const fetchUser = async () => {
      try {
        const response = await axios.get(`${API_ENDPOINTS.GET_ERROR_STUDENT}/${userId}`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        setUserData(response.data);
        setFormData(response.data);
      } catch (error) {
        if (axios.isAxiosError(error)) {
          if (error.response?.status === 401) {
            logout();
            navigate("/login");
          } else {
            console.error("Error fetching user data", error);
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
    if (!formData || !formData.applicantId) {
      setErrorMessage("Поле applicant_id обязательно");
      return;
    }

    try {
      await axios.post(`${API_ENDPOINTS.FIX_ERROR_STUDENT}/${userId}/fix`, formData, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      setIsEditing(false);
      setUserData(formData);
      setErrorMessage(null); // очистить ошибки
    } catch (error) {
      if (axios.isAxiosError(error)) {
        if (error.response?.status === 401) {
          logout();
          navigate("/login");
        } else {
          const serverError = error.response?.data?.error || "Ошибка при сохранении";
          setErrorMessage(serverError);
        }
      } else {
        setErrorMessage("Непредвиденная ошибка");
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
              src={BACKEND_URL + userData.base64}
              alt={`${userData.name || "No Name"} ${userData.surname || "No Surname"}`}
              className="userAvatar"
          />
          <div className="userDetails">
            <p>
              <strong>ID Абитуриента:</strong>{" "}
              {isEditing ? (
                  <input
                      type="number"
                      name="applicantId"
                      value={formData?.applicantId || ""}
                      onChange={handleChange}
                  />
              ) : (
                  userData.applicantId
              )}
            </p>
            <p>
              <strong>Имя:</strong>{" "}
              {isEditing ? (
                  <input type="text" name="name" value={formData?.name || ""} onChange={handleChange} />
              ) : (
                  userData.name
              )}
            </p>
            <p>
              <strong>Фамилия:</strong>{" "}
              {isEditing ? (
                  <input
                      type="text"
                      name="surname"
                      value={formData?.surname || ""}
                      onChange={handleChange}
                  />
              ) : (
                  userData.surname
              )}
            </p>
            <p>
              <strong>Номер телефона:</strong>{" "}
              {isEditing ? (
                  <input
                      type="text"
                      name="phoneNum"
                      value={formData?.phoneNum || ""}
                      onChange={handleChange}
                  />
              ) : (
                  userData.phoneNum
              )}
            </p>
            <p>
              <strong>Школа:</strong>{" "}
              {isEditing ? (
                  <input
                      type="text"
                      name="school"
                      value={formData?.school || ""}
                      onChange={handleChange}
                  />
              ) : (
                  userData.school
              )}
            </p>
            <p>
              <strong>Количество попыток:</strong>{" "}
              {isEditing ? (
                  <input
                      type="number"
                      name="attempt"
                      value={formData?.attempt || 0}
                      onChange={handleChange}
                  />
              ) : (
                  userData.attempt
              )}
            </p>
            <p>
              <strong>Статус:</strong>{" "}
              {isEditing ? (
                  <input
                      type="text"
                      name="status"
                      value={formData?.status || ""}
                      onChange={handleChange}
                  />
              ) : (
                  userData.status
              )}
            </p>
            <p>
              <strong>Created At:</strong> {new Date(userData.created_at).toLocaleString()}
            </p>
          </div>
        </div>

        <div className="userActions">
          {isEditing ? (
              <>
                <button onClick={handleSave}>Сохранить</button>
                <button onClick={() => setIsEditing(false)}>Отмена</button>
              </>
          ) : (
              <button onClick={() => setIsEditing(true)}>Изменить и сохранить как абитуриента</button>
          )}
          {errorMessage && <p style={{ color: "red" }}>{errorMessage}</p>}
        </div>
      </div>
  );
};

export default ErrorStudent;
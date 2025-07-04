import { useState } from "react";
import { Box, Button, TextField, Typography, Paper } from "@mui/material";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import axios from "axios";
import API_ENDPOINTS from "../../api/endpoints";

const Login = () => {
  const { login } = useAuth();
  const navigate = useNavigate();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  const handleLogin = async () => {
    try {
      const response = await axios.post(API_ENDPOINTS.LOGIN, {
        email,
        password,
      });

      console.log("Ответ сервера:", response.data);

      const token = response.data?.access; // <-- берем access, а не token
      if (token) {
        login(token);
        navigate("/");
      } else {
        setError("Неверный ответ от сервера.");
      }
    } catch (err: any) {
      console.log("Ошибка запроса:", err.response?.data || err.message);
      setError("Ошибка авторизации. Проверьте логин и пароль.");
    }
  };

  return (
      <Box display="flex" justifyContent="center" alignItems="center" height="100vh">
        <Paper elevation={3} sx={{ padding: 4, width: 400 }}>
          <Typography variant="h5" gutterBottom>Вход в систему</Typography>
          <TextField
              fullWidth
              label="Логин"
              margin="normal"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
          />
          <TextField
              fullWidth
              label="Пароль"
              type="password"
              margin="normal"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
          />
          {error && <Typography color="error">{error}</Typography>}
          <Button
              fullWidth
              variant="contained"
              color="primary"
              sx={{ mt: 2 }}
              onClick={handleLogin}
          >
            Войти
          </Button>
        </Paper>
      </Box>
  );
};

export default Login;
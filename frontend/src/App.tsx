import Home from "./pages/home/Home";
import { createBrowserRouter, RouterProvider, Outlet } from "react-router-dom";
import Users from "./pages/users/Users";
import Navbar from "./components/navbar/Navbar";
import Menu from "./components/menu/Menu";
import Login from "./pages/login/Login";
import "./styles/global.scss";
import User from "./pages/user/User";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import {AuthProvider} from "./context/AuthContext.tsx";
import ProtectedRoute from "./components/rotectedRoute/ProtectedRoute.tsx";
import ErrorStudents from "./pages/errorStudents/ErrorStudents";
import ErrorStudent from "./pages/errorStudent/ErrorStudent";
import BanStudents from "./pages/banStudents/BanStudents.tsx";
import BanStudent from "./pages/banStudent/BanStudent.tsx";
import Check from "./pages/check/Check.tsx";
import ExamsPage from "./pages/exams/ExamsPage.tsx";

const queryClient = new QueryClient();

function App() {
  const Layout = () => {
    return (
        <div className="main">
          <Navbar />
          <div className="container">
            <div className="menuContainer">
              <Menu />
            </div>
            <div className="contentContainer">
              <QueryClientProvider client={queryClient}>
                <Outlet />
              </QueryClientProvider>
            </div>
          </div>
        </div>
    );
  };

  const router = createBrowserRouter([
    {
      path: "/",
      element: (
          <ProtectedRoute>
            <Layout />
          </ProtectedRoute>
      ),
      children: [
        { path: "/", element: <Home /> },
        { path: "/students", element: <Users /> },
        { path: "/users/:id", element: <User /> },
        { path: "/error-students", element: <ErrorStudents /> },
        { path: "/error-students/:id", element: <ErrorStudent /> },
        { path: "/blacklist", element: <BanStudents /> },
        { path: "/blacklist/:id", element: <BanStudent /> },
        { path: "/exams", element: <ExamsPage /> },
      ],
    },
    {
      path: "/login",
      element: <Login />,
    },
    {
      path: "/check",
      element: <Check />,
    },
  ]);

  return (
      <AuthProvider>
        <RouterProvider router={router} />
      </AuthProvider>
  );
}

export default App;

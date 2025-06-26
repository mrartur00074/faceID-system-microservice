import { DataGrid, GridColDef, GridToolbar } from "@mui/x-data-grid";
import "./dataTable.scss";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import { API_ENDPOINTS } from "../../api/endpoints";
import {useState} from "react";
import axios from "axios";

type Props = {
    columns: GridColDef[];
    rows: object[];
    slug: string;
    errors: boolean;
    blacklist: boolean;
};

const DataTable = (props: Props) => {
    const { user, logout } = useAuth();
    const token = user?.token;
    const navigate = useNavigate();
    const [rows, setRows] = useState(props.rows);

    const handleBlacklist = async (id: number, applicant_id: number) => {
        try {
            const targetId = props.errors ? id : applicant_id;

            const response = await axios.post(
                `${API_ENDPOINTS.BLACKLIST_APPLICANT}/${targetId}/blacklist/`,
                {},
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                }
            );

            console.log(response)

            setRows((prevRows: any[]) => prevRows.filter((row) => row.id !== id));
            console.log(`Поступающий с ID ${targetId} добавлен в чёрный список.`);
        } catch (err: any) {
            if (err.response && err.response.status === 401) {
                logout();
                navigate("/login");
            } else {
                console.error("Ошибка при добавлении в чёрный список:", err.message);
                alert("Ошибка при добавлении в чёрный список");
            }
        }
    };

    const handleDelete = async (id: number, applicant_id: String) => {
        try {
            const targetId = props.errors ? id : applicant_id;
            let endpoint = "";

            if (props.errors) {
                endpoint = `${API_ENDPOINTS.DELETE_ERROR_STUDENT}/${targetId}/`;
            } else if (props.blacklist) {
                endpoint = `${API_ENDPOINTS.DELETE_BAN_STUDENT}/${targetId}/`;
            } else {
                endpoint = `${API_ENDPOINTS.DELETE_APPLICANT}/${targetId}/`;
            }

            const response = await fetch(endpoint, {
                method: "DELETE",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                },
            });

            if (!response.ok) {
                if (response.status === 401) {
                    logout(); // Если токен невалиден, выходим из аккаунта
                    navigate("/login"); // Перенаправляем на страницу логина
                } else {
                    const errorText = await response.text();
                    throw new Error(errorText || "Ошибка при удалении.");
                }
            }

            setRows((prevRows: any[]) => prevRows.filter((row) => row.id !== id));

            // Обработка успешного ответа
            console.log(`Поступающий с ID ${applicant_id} успешно удалён.`);

        } catch (err: any) {
            console.error("Ошибка:", err.message);
        }
    };

    const actionColumn: GridColDef = {
        field: "action",
        headerName: "Action",
        width: 150,
        renderCell: (params) => {
            const idForLink = props.errors ? params.row.id : params.row.applicant_id;

            return (
                <div className="action">
                    <Link to={`/${props.slug}/${idForLink}`}>
                        <img src="/view.svg" alt="View" />
                    </Link>
                    <div
                        className="delete"
                        onClick={() => handleDelete(params.row.id, params.row.applicant_id)}
                    >
                        <img src="/delete.svg" alt="Delete" />
                    </div>
                    {!props.blacklist && (
                        <div
                            className="blacklist"
                            onClick={() => handleBlacklist(params.row.id, params.row.applicant_id)}
                        >
                            <img src="/ban.svg" alt="Blacklist" />
                        </div>
                    )}
                </div>
            );
        },
    };

    return (
        <div className="dataTable">
            <DataGrid
                className="dataGrid"
                rows={rows}
                columns={[...props.columns, actionColumn]} // Добавляем колонку с действиями
                initialState={{
                    pagination: {
                        paginationModel: {
                            pageSize: 50,
                        },
                    },
                    sorting: {
                        sortModel: [{ field: "applicant_id", sort: "asc" }],
                    },
                }}
                slots={{ toolbar: GridToolbar }}
                slotProps={{
                    toolbar: {
                        showQuickFilter: true,
                        quickFilterProps: { debounceMs: 500 },
                    },
                }}
                pageSizeOptions={[5]}
                checkboxSelection
                disableRowSelectionOnClick
                disableColumnFilter
                disableDensitySelector
                disableColumnSelector
            />
        </div>
    );
};

export default DataTable;
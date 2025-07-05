const BASE_URL = import.meta.env.VITE_BASE_URL;

export const API_ENDPOINTS = {
    LOGIN: `${BASE_URL}/admins/login/`,
    LOGOUT: `${BASE_URL}/admins/logout/`,
    GET_STUDENTS: `${BASE_URL}/applicants`,
    GET_APPLICANT: `${BASE_URL}/applicant/applicants`,
    ADD_APPLICANT: `${BASE_URL}/applicant/applicants/upload/`,
    DELETE_APPLICANT: `${BASE_URL}/applicant/applicants`,
    DELETE_ERROR_STUDENT: `${BASE_URL}/applicant/errors`,
    GET_ERROR_STUDENTS: `${BASE_URL}/applicant/errors/`,
    GET_ERROR_STUDENT: `${BASE_URL}/applicant/errors`,
    FIX_ERROR_STUDENT: `${BASE_URL}/applicant/errors`,
    GET_BAN_STUDENTS: `${BASE_URL}/applicant/blacklist/`,
    GET_BAN_STUDENT: `${BASE_URL}/applicant/blacklist`,
    BLACKLIST_APPLICANT: `${BASE_URL}/applicant/applicants`,
    RESTORE_BLACKLIST: `${BASE_URL}/applicant/blacklist`,
    DELETE_BAN_STUDENT: `${BASE_URL}/applicant/blacklist`,
    SEARCH_APPLICANT: `${BASE_URL}/applicant/applicants/search/`,
    EXAMS: `${BASE_URL}/exams/exams/`,
};

export default API_ENDPOINTS;
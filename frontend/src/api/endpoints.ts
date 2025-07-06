const BASE_URL = import.meta.env.VITE_BASE_URL;

export const API_ENDPOINTS = {
    LOGIN: `${BASE_URL}/admins/login/`,
    LOGOUT: `${BASE_URL}/admins/logout/`,
    GET_STUDENTS: `${BASE_URL}/applicants`,
    GET_APPLICANT: `${BASE_URL}/applicants`,
    ADD_APPLICANT: `${BASE_URL}/applicants/add`,
    DELETE_APPLICANT: `${BASE_URL}/applicants`,
    DELETE_ERROR_STUDENT: `${BASE_URL}/error-applicants`,
    GET_ERROR_STUDENTS: `${BASE_URL}/error-applicants`,
    GET_ERROR_STUDENT: `${BASE_URL}/error-applicants`,
    FIX_ERROR_STUDENT: `${BASE_URL}/error-applicants`,
    GET_BAN_STUDENTS: `${BASE_URL}/blacklist`,
    GET_BAN_STUDENT: `${BASE_URL}/blacklist`,
    BLACKLIST_APPLICANT: `${BASE_URL}/applicants`,
    RESTORE_BLACKLIST: `${BASE_URL}/blacklist`,
    DELETE_BAN_STUDENT: `${BASE_URL}/blacklist`,
    SEARCH_APPLICANT: `${BASE_URL}/search`,
    EXAMS: `${BASE_URL}/exams/exams`,
};

export default API_ENDPOINTS;
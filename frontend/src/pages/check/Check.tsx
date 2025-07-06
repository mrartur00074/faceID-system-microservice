import React, { useRef, useState, useEffect } from "react";
import {
    Box,
    Button,
    Card,
    CardContent,
    CardMedia,
    Grid,
    TextField,
    Typography,
    Alert,
} from "@mui/material";
import { API_ENDPOINTS } from "../../api/endpoints";

const BACKEND_URL = import.meta.env.VITE_BACKEND_URL;

const Check = () => {
    const videoRef = useRef<HTMLVideoElement>(null);
    const canvasRef = useRef<HTMLCanvasElement>(null);
    const isRecognizingRef = useRef(false);

    const [isRecognizing, setIsRecognizing] = useState(false);
    const [intervalId, setIntervalId] = useState<NodeJS.Timeout | null>(null);
    const [query, setQuery] = useState("");
    const [inputDisabled, setInputDisabled] = useState(false);
    const [results, setResults] = useState<any[]>([]);
    const [currentUser, setCurrentUser] = useState<any | null>(null);
    const [facingMode, setFacingMode] = useState<"user" | "environment">("environment");
    const [errorMessage, setErrorMessage] = useState<string | null>(null);

    useEffect(() => {
        let animationFrameId: number;

        const draw = () => {
            const canvas = canvasRef.current;
            const video = videoRef.current;
            if (!canvas || !video || video.videoWidth === 0) {
                animationFrameId = requestAnimationFrame(draw);
                return;
            }

            const ctx = canvas.getContext("2d");
            if (!ctx) return;

            canvas.width = video.videoWidth;
            canvas.height = video.videoHeight;

            ctx.clearRect(0, 0, canvas.width, canvas.height);
            ctx.font = "bold 32px Arial";
            ctx.fillStyle = "lime";
            ctx.fillText(currentUser?.applicantId || "Неизвестный ID", 20, 40);

            animationFrameId = requestAnimationFrame(draw);
        };

        draw();

        return () => cancelAnimationFrame(animationFrameId);
    }, [currentUser]);

    const sendSearchRequest = async (base64Image?: string) => {
        const payload: any = {};
        if (query.trim()) {
            payload.applicantId = query.trim();
        } else if (base64Image) {
            payload.base64Image = base64Image;
        } else {
            return;
        }

        try {
            const response = await fetch(API_ENDPOINTS.SEARCH_APPLICANT, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload),
            });

            const data = await response.json();

            if (!isRecognizingRef.current && !query) return;

            if (Array.isArray(data?.applicants) && data?.applicants.length > 0) {
                setResults(data?.applicants);
                setCurrentUser(data?.applicants[0]);
                stopRecognition();
            } else if (data?.applicants?.applicantId) {
                setResults([data?.applicants]);
                setCurrentUser(data?.applicants);
                stopRecognition();
            } else {
                setResults([]);
                setCurrentUser(null);
            }
        } catch (err) {
            console.error("Ошибка при поиске:", err);
            setErrorMessage("Произошла ошибка при распознавании. Попробуйте ещё раз.");
            setResults([]);
            setCurrentUser(null);
        }
    };

    const startRecognition = async () => {
        setQuery("");
        setErrorMessage(null);
        setInputDisabled(true);
        isRecognizingRef.current = true;
        setIsRecognizing(true);
        setResults([]);
        setCurrentUser(null);

        try {
            const stream = await navigator.mediaDevices.getUserMedia({
            video: { 
                facingMode,
                width: { ideal: 640 },  // Ограничиваем максимальное разрешение
                height: { ideal: 480 }
                },
            });

            if (videoRef.current) {
                videoRef.current.srcObject = stream;
            }

            const id = setInterval(async () => {
                if (!isRecognizingRef.current || !videoRef.current?.srcObject) return;

                const video = videoRef.current;
                if (video.videoWidth === 0 || video.videoHeight === 0) return;

                const canvas = document.createElement("canvas");
                canvas.width = video.videoWidth / 2;
                canvas.height = video.videoHeight / 2;

                const ctx = canvas.getContext("2d");
                if (!ctx) return;

                ctx.drawImage(video, 0, 0, canvas.width, canvas.height);
                const base64Image = canvas.toDataURL("image/jpeg", 0.8).split(",")[1];

                await sendSearchRequest(base64Image);
            }, 1500);

            setIntervalId(id);
        } catch (err) {
            console.error("Ошибка доступа к камере:", err);
            setErrorMessage("Не удалось включить камеру. Разрешите доступ или попробуйте другой браузер.");
            stopRecognition();
        }
    };

    const stopRecognition = () => {
        if (intervalId) clearInterval(intervalId);
        if (videoRef.current?.srcObject) {
            (videoRef.current.srcObject as MediaStream)
                .getTracks()
                .forEach((track) => track.stop());
        }

        setIntervalId(null);
        isRecognizingRef.current = false;
        setIsRecognizing(false);
        setInputDisabled(false);
    };

    const toggleCamera = () => {
        setFacingMode((prev) => (prev === "user" ? "environment" : "user"));
    };

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const value = e.target.value;
        if (value.length > 0 && isRecognizing) {
            stopRecognition();
        }
        setQuery(value);
    };

    const handleSearchClick = () => {
        setErrorMessage(null);
        setResults([]);
        setCurrentUser(null);
        sendSearchRequest();
    };

    return (
        <div className="check">
            <Box p={2}>
                <Typography variant="h4" mb={2}>
                    Распознавание студентов
                </Typography>

                <Box display="flex" alignItems="center" gap={2} mb={2} flexWrap="wrap">
                    <TextField
                        label="Поиск по имени или ID"
                        value={query}
                        onChange={handleInputChange}
                        disabled={inputDisabled}
                    />
                    <Button
                        variant="contained"
                        onClick={handleSearchClick}
                        disabled={isRecognizing || !query}
                    >
                        Искать
                    </Button>

                    {!isRecognizing ? (
                        <Button
                            variant="contained"
                            color="success"
                            onClick={startRecognition}
                            disabled={!!query}
                        >
                            Начать распознавание
                        </Button>
                    ) : (
                        <Button variant="contained" color="error" onClick={stopRecognition}>
                            Остановить
                        </Button>
                    )}

                    <Button variant="outlined" onClick={toggleCamera}>
                        Сменить камеру ({facingMode === "user" ? "Фронтальная" : "Задняя"})
                    </Button>
                </Box>

                {errorMessage && (
                    <Alert severity="error" sx={{ mb: 2 }}>
                        {errorMessage}
                    </Alert>
                )}

                {currentUser && (
                    <Card sx={{ maxWidth: 400, mb: 3 }}>
                        <CardMedia
                            component="img"
                            height="400"
                            image={BACKEND_URL + currentUser.base64}
                            alt="Распознанный студент"
                        />
                        <CardContent>
                            <Typography variant="h6">
                                {currentUser.name} {currentUser.surname}
                            </Typography>
                            <Typography>ID: {currentUser.applicantId}</Typography>
                            {currentUser.school && (
                                <Typography>Школа: {currentUser.school}</Typography>
                            )}
                            {currentUser.status && (
                                <Typography>Статус: {currentUser.status}</Typography>
                            )}
                        </CardContent>
                    </Card>
                )}

                <Box position="relative" width="640px" height="480px" mb={4}>
                    <video
                        ref={videoRef}
                        autoPlay
                        muted
                        playsInline
                        width="640"
                        height="480"
                        style={{ borderRadius: 8 }}
                    />
                    <canvas
                        ref={canvasRef}
                        style={{
                            position: "absolute",
                            top: 0,
                            left: 0,
                            zIndex: 10,
                            width: "100%",
                            height: "100%",
                            pointerEvents: "none",
                        }}
                    />
                </Box>

                {results.length > 1 && (
                    <Box mt={2}>
                        <Typography variant="h6">Другие совпадения:</Typography>
                        <Grid container spacing={2} mt={1}>
                            {results.slice(1).map((item) => (
                                <Grid item xs={12} sm={6} md={4} lg={3} key={item.applicantId}>
                                    <Card>
                                        <CardMedia
                                            component="img"
                                            height="200"
                                            image={BACKEND_URL + currentUser.base64}
                                            alt="Student"
                                        />
                                        <CardContent>
                                            <Typography variant="h6">
                                                {item.name} {item.surname}
                                            </Typography>
                                            <Typography>ID: {item.applicantId}</Typography>
                                        </CardContent>
                                    </Card>
                                </Grid>
                            ))}
                        </Grid>
                    </Box>
                )}
            </Box>
        </div>
    );
};

export default Check;

export interface ApiError {
    status: number;
    error: string;
    message: string;
    timestamp: string;
    validationErrors?: Record<string, string>;
}

export interface ApiResponse<T = any> {
    success: boolean;
    data?: T;
    error?: ApiError;
}
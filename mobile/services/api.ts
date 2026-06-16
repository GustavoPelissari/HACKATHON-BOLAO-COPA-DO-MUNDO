import AsyncStorage from "@react-native-async-storage/async-storage";
import axios from "axios";
import Constants from "expo-constants";

// Base da API. Em emulador Android, "localhost" do PC e acessado via 10.0.2.2.
// Ajuste em app.json (extra.apiBaseUrl) ou aponte para o backend publicado.
const BASE_URL =
    (Constants.expoConfig?.extra?.apiBaseUrl as string) ?? "http://10.0.2.2:8080";

export const TOKEN_KEY = "@bolao_copa:token";

const api = axios.create({
    baseURL: BASE_URL,
    timeout: 10_000,
});

// Injeta automaticamente o token JWT em todas as requisicoes autenticadas.
api.interceptors.request.use(async (config) => {
    const token = await AsyncStorage.getItem(TOKEN_KEY);
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

export { api };

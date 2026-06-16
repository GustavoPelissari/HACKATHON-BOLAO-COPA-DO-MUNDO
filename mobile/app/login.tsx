import { Ionicons } from "@expo/vector-icons";
import { useRouter } from "expo-router";
import { useState } from "react";
import {
    Alert,
    KeyboardAvoidingView,
    Platform,
    StyleSheet,
    Text,
    TextInput,
    TouchableOpacity,
    View,
} from "react-native";
import { useAuth } from "../contexts/AuthContext";
import { recuperarSenha } from "../services/authService";
import { cores } from "../theme/cores";
import { mensagemDeErro } from "../utils/erros";

export default function LoginScreen() {
    const router = useRouter();
    const { entrar } = useAuth();

    const [email, setEmail] = useState("");
    const [senha, setSenha] = useState("");
    const [secureText, setSecureText] = useState(true);
    const [carregando, setCarregando] = useState(false);

    async function clicouEmEntrar() {
        if (!email || !senha) {
            Alert.alert("Atencao!", "Informe e-mail e senha.");
            return;
        }

        try {
            setCarregando(true);
            await entrar(email.trim(), senha);
            router.replace("/(tabs)/partidas");
        } catch (erro) {
            Alert.alert("Nao foi possivel entrar", mensagemDeErro(erro));
        } finally {
            setCarregando(false);
        }
    }

    async function clicouEmEsqueciSenha() {
        if (!email) {
            Alert.alert("Recuperar senha", "Informe seu e-mail no campo acima primeiro.");
            return;
        }
        try {
            const msg = await recuperarSenha(email.trim());
            Alert.alert("Recuperar senha", msg);
        } catch (erro) {
            Alert.alert("Atencao!", mensagemDeErro(erro));
        }
    }

    return (
        <KeyboardAvoidingView
            behavior={Platform.OS === "ios" ? "padding" : "height"}
            style={styles.container}
        >
            <View style={styles.inner}>
                <Text style={styles.logo}>⚽</Text>
                <Text style={styles.title}>Bolao Copa 2026</Text>
                <Text style={styles.subtitle}>Faca seus palpites e suba no ranking</Text>

                <Text style={styles.label}>E-mail</Text>
                <TextInput
                    style={styles.input}
                    placeholder="email@exemplo.com"
                    placeholderTextColor={cores.textoClaro}
                    keyboardType="email-address"
                    autoCapitalize="none"
                    value={email}
                    onChangeText={setEmail}
                />

                <Text style={styles.label}>Senha</Text>
                <View style={styles.passwordContainer}>
                    <TextInput
                        style={styles.passwordInput}
                        placeholder="********"
                        placeholderTextColor={cores.textoClaro}
                        secureTextEntry={secureText}
                        value={senha}
                        onChangeText={setSenha}
                    />
                    <TouchableOpacity
                        onPress={() => setSecureText((v) => !v)}
                        style={styles.iconContainer}
                    >
                        <Ionicons
                            name={secureText ? "eye-off-outline" : "eye-outline"}
                            size={20}
                            color={cores.textoClaro}
                        />
                    </TouchableOpacity>
                </View>

                <TouchableOpacity onPress={clicouEmEsqueciSenha} style={styles.esqueci}>
                    <Text style={styles.esqueciTexto}>Esqueci minha senha</Text>
                </TouchableOpacity>

                <TouchableOpacity
                    style={[styles.button, carregando && styles.buttonDisabled]}
                    onPress={clicouEmEntrar}
                    disabled={carregando}
                >
                    <Text style={styles.buttonText}>{carregando ? "Entrando..." : "Entrar"}</Text>
                </TouchableOpacity>

                <View style={styles.rodape}>
                    <Text style={styles.rodapeTexto}>Ainda nao tem conta? </Text>
                    <TouchableOpacity onPress={() => router.push("/cadastro")}>
                        <Text style={styles.rodapeLink}>Cadastre-se</Text>
                    </TouchableOpacity>
                </View>
            </View>
        </KeyboardAvoidingView>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: cores.cartao,
    },
    inner: {
        flex: 1,
        justifyContent: "center",
        paddingHorizontal: 24,
    },
    logo: {
        fontSize: 56,
        textAlign: "center",
    },
    title: {
        fontSize: 26,
        fontWeight: "800",
        color: cores.primaria,
        textAlign: "center",
        marginTop: 8,
    },
    subtitle: {
        fontSize: 14,
        color: cores.textoClaro,
        textAlign: "center",
        marginBottom: 32,
    },
    label: {
        fontSize: 14,
        fontWeight: "600",
        color: cores.textoClaro,
        marginBottom: 6,
    },
    input: {
        height: 52,
        borderWidth: 1,
        borderColor: cores.borda,
        borderRadius: 12,
        paddingHorizontal: 15,
        fontSize: 16,
        color: cores.texto,
        backgroundColor: "#fbfbfd",
        marginBottom: 16,
    },
    passwordContainer: {
        flexDirection: "row",
        height: 52,
        borderWidth: 1,
        borderColor: cores.borda,
        borderRadius: 12,
        backgroundColor: "#fbfbfd",
        overflow: "hidden",
    },
    passwordInput: {
        flex: 1,
        paddingHorizontal: 15,
        fontSize: 16,
        color: cores.texto,
    },
    iconContainer: {
        justifyContent: "center",
        paddingHorizontal: 15,
    },
    esqueci: {
        alignSelf: "flex-end",
        marginTop: 10,
    },
    esqueciTexto: {
        color: cores.primaria,
        fontSize: 13,
        fontWeight: "600",
    },
    button: {
        height: 52,
        borderRadius: 12,
        justifyContent: "center",
        alignItems: "center",
        marginTop: 24,
        backgroundColor: cores.primaria,
    },
    buttonDisabled: {
        opacity: 0.6,
    },
    buttonText: {
        fontSize: 16,
        fontWeight: "bold",
        color: "#FFF",
    },
    rodape: {
        flexDirection: "row",
        justifyContent: "center",
        marginTop: 24,
    },
    rodapeTexto: {
        color: cores.textoClaro,
    },
    rodapeLink: {
        color: cores.primaria,
        fontWeight: "700",
    },
});

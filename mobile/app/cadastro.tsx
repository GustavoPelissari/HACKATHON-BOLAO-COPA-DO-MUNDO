import { Ionicons } from "@expo/vector-icons";
import { useRouter } from "expo-router";
import { useState } from "react";
import {
    Alert,
    KeyboardAvoidingView,
    Platform,
    ScrollView,
    StyleSheet,
    Text,
    TextInput,
    TouchableOpacity,
    View,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { useAuth } from "../contexts/AuthContext";
import { cadastrar } from "../services/authService";
import { cores } from "../theme/cores";
import { mensagemDeErro } from "../utils/erros";

export default function CadastroScreen() {
    const router = useRouter();
    const { entrarComToken } = useAuth();

    const [nome, setNome] = useState("");
    const [email, setEmail] = useState("");
    const [senha, setSenha] = useState("");
    const [carregando, setCarregando] = useState(false);

    async function clicouEmCadastrar() {
        if (!nome || !email || !senha) {
            Alert.alert("Atencao!", "Preencha todos os campos.");
            return;
        }
        if (senha.length < 6) {
            Alert.alert("Atencao!", "A senha deve ter no minimo 6 caracteres.");
            return;
        }

        try {
            setCarregando(true);
            const resposta = await cadastrar(nome.trim(), email.trim(), senha);
            await entrarComToken(resposta.token);
            router.replace("/(tabs)/partidas");
        } catch (erro) {
            Alert.alert("Nao foi possivel cadastrar", mensagemDeErro(erro));
        } finally {
            setCarregando(false);
        }
    }

    return (
        <SafeAreaView style={styles.container} edges={["top", "left", "right"]}>
            <KeyboardAvoidingView
                behavior={Platform.OS === "ios" ? "padding" : "height"}
                style={{ flex: 1 }}
            >
                <ScrollView contentContainerStyle={styles.scroll} keyboardShouldPersistTaps="handled">
                    <TouchableOpacity onPress={() => router.back()} style={styles.voltar}>
                        <Ionicons name="arrow-back" size={24} color={cores.primaria} />
                    </TouchableOpacity>

                    <Text style={styles.title}>Criar conta</Text>
                    <Text style={styles.subtitle}>Leva menos de um minuto</Text>

                    <Text style={styles.label}>Nome</Text>
                    <TextInput
                        style={styles.input}
                        placeholder="Seu nome"
                        placeholderTextColor={cores.textoClaro}
                        value={nome}
                        onChangeText={setNome}
                    />

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
                    <TextInput
                        style={styles.input}
                        placeholder="Minimo 6 caracteres"
                        placeholderTextColor={cores.textoClaro}
                        secureTextEntry
                        value={senha}
                        onChangeText={setSenha}
                    />

                    <TouchableOpacity
                        style={[styles.button, carregando && styles.buttonDisabled]}
                        onPress={clicouEmCadastrar}
                        disabled={carregando}
                    >
                        <Text style={styles.buttonText}>
                            {carregando ? "Criando..." : "Cadastrar"}
                        </Text>
                    </TouchableOpacity>
                </ScrollView>
            </KeyboardAvoidingView>
        </SafeAreaView>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: cores.cartao,
    },
    scroll: {
        padding: 24,
        flexGrow: 1,
    },
    voltar: {
        marginBottom: 16,
    },
    title: {
        fontSize: 26,
        fontWeight: "800",
        color: cores.primaria,
    },
    subtitle: {
        fontSize: 14,
        color: cores.textoClaro,
        marginBottom: 28,
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
    button: {
        height: 52,
        borderRadius: 12,
        justifyContent: "center",
        alignItems: "center",
        marginTop: 12,
        backgroundColor: cores.destaque,
    },
    buttonDisabled: {
        opacity: 0.6,
    },
    buttonText: {
        fontSize: 16,
        fontWeight: "bold",
        color: "#FFF",
    },
});

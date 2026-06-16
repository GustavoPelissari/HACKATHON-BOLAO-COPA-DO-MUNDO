import { FontAwesome } from "@expo/vector-icons";
import { useRouter } from "expo-router";
import { useState } from "react";
import {
    Alert,
    Image,
    ScrollView,
    StyleSheet,
    Text,
    TextInput,
    TouchableOpacity,
    View,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { useAuth } from "../../contexts/AuthContext";
import { atualizarPerfil, excluirConta } from "../../services/authService";
import { cores } from "../../theme/cores";
import { mensagemDeErro } from "../../utils/erros";

export default function PerfilScreen() {
    const router = useRouter();
    const { usuario, atualizarUsuario, sair } = useAuth();

    const [editando, setEditando] = useState(false);
    const [nome, setNome] = useState(usuario?.nome ?? "");
    const [avatarUrl, setAvatarUrl] = useState(usuario?.avatarUrl ?? "");
    const [salvando, setSalvando] = useState(false);

    if (!usuario) {
        return null;
    }

    async function salvar() {
        if (!nome.trim()) {
            Alert.alert("Atencao!", "O nome nao pode ficar vazio.");
            return;
        }
        try {
            setSalvando(true);
            const atualizado = await atualizarPerfil(nome.trim(), avatarUrl || null);
            atualizarUsuario(atualizado);
            setEditando(false);
            Alert.alert("Pronto!", "Perfil atualizado.");
        } catch (erro) {
            Alert.alert("Atencao!", mensagemDeErro(erro));
        } finally {
            setSalvando(false);
        }
    }

    function confirmarSair() {
        Alert.alert("Sair", "Deseja encerrar a sessao?", [
            { text: "Cancelar", style: "cancel" },
            {
                text: "Sair",
                style: "destructive",
                onPress: async () => {
                    await sair();
                    router.replace("/login");
                },
            },
        ]);
    }

    function confirmarExclusao() {
        Alert.alert(
            "Excluir conta",
            "Esta acao e permanente e remove todos os seus dados (LGPD). Deseja continuar?",
            [
                { text: "Cancelar", style: "cancel" },
                {
                    text: "Excluir",
                    style: "destructive",
                    onPress: async () => {
                        try {
                            await excluirConta();
                            await sair();
                            router.replace("/login");
                        } catch (erro) {
                            Alert.alert("Atencao!", mensagemDeErro(erro));
                        }
                    },
                },
            ]
        );
    }

    return (
        <SafeAreaView style={styles.container} edges={["top", "left", "right"]}>
            <ScrollView contentContainerStyle={styles.scroll}>
                <View style={styles.cabecalho}>
                    {usuario.avatarUrl ? (
                        <Image source={{ uri: usuario.avatarUrl }} style={styles.avatar} />
                    ) : (
                        <View style={[styles.avatar, styles.avatarVazio]}>
                            <Text style={styles.avatarInicial}>
                                {usuario.nome.charAt(0).toUpperCase()}
                            </Text>
                        </View>
                    )}
                    <Text style={styles.nome}>{usuario.nome}</Text>
                    <Text style={styles.email}>{usuario.email}</Text>
                </View>

                <View style={styles.estatisticas}>
                    <View style={styles.estatItem}>
                        <Text style={styles.estatValor}>{usuario.pontuacaoTotal}</Text>
                        <Text style={styles.estatLabel}>Pontos</Text>
                    </View>
                    <View style={styles.divisor} />
                    <View style={styles.estatItem}>
                        <Text style={styles.estatValor}>{usuario.placaresExatos}</Text>
                        <Text style={styles.estatLabel}>Placares exatos</Text>
                    </View>
                </View>

                {editando ? (
                    <View style={styles.formCard}>
                        <Text style={styles.label}>Nome de exibicao</Text>
                        <TextInput style={styles.input} value={nome} onChangeText={setNome} />

                        <Text style={styles.label}>URL do avatar</Text>
                        <TextInput
                            style={styles.input}
                            value={avatarUrl}
                            onChangeText={setAvatarUrl}
                            placeholder="https://..."
                            placeholderTextColor={cores.textoClaro}
                            autoCapitalize="none"
                        />

                        <View style={styles.formAcoes}>
                            <TouchableOpacity
                                style={[styles.botao, styles.botaoSalvar]}
                                onPress={salvar}
                                disabled={salvando}
                            >
                                <Text style={styles.botaoSalvarTexto}>
                                    {salvando ? "Salvando..." : "Salvar"}
                                </Text>
                            </TouchableOpacity>
                            <TouchableOpacity
                                style={[styles.botao, styles.botaoCancelar]}
                                onPress={() => setEditando(false)}
                            >
                                <Text style={styles.botaoCancelarTexto}>Cancelar</Text>
                            </TouchableOpacity>
                        </View>
                    </View>
                ) : (
                    <View style={styles.menu}>
                        <ItemMenu icone="pencil" texto="Editar perfil" onPress={() => setEditando(true)} />
                        <ItemMenu icone="sign-out" texto="Sair" onPress={confirmarSair} />
                        <ItemMenu
                            icone="trash"
                            texto="Excluir minha conta"
                            destrutivo
                            onPress={confirmarExclusao}
                        />
                    </View>
                )}
            </ScrollView>
        </SafeAreaView>
    );
}

function ItemMenu({
    icone,
    texto,
    onPress,
    destrutivo = false,
}: {
    icone: keyof typeof FontAwesome.glyphMap;
    texto: string;
    onPress: () => void;
    destrutivo?: boolean;
}) {
    return (
        <TouchableOpacity style={styles.itemMenu} onPress={onPress}>
            <FontAwesome
                name={icone}
                size={18}
                color={destrutivo ? cores.erro : cores.primaria}
                style={{ width: 26 }}
            />
            <Text style={[styles.itemTexto, destrutivo && { color: cores.erro }]}>{texto}</Text>
            <FontAwesome name="angle-right" size={20} color={cores.textoClaro} />
        </TouchableOpacity>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: cores.fundo,
    },
    scroll: {
        padding: 20,
    },
    cabecalho: {
        alignItems: "center",
        marginTop: 12,
        marginBottom: 24,
    },
    avatar: {
        width: 96,
        height: 96,
        borderRadius: 48,
        marginBottom: 12,
    },
    avatarVazio: {
        backgroundColor: cores.primaria,
        justifyContent: "center",
        alignItems: "center",
    },
    avatarInicial: {
        color: "#fff",
        fontWeight: "800",
        fontSize: 38,
    },
    nome: {
        fontSize: 22,
        fontWeight: "800",
        color: cores.texto,
    },
    email: {
        fontSize: 14,
        color: cores.textoClaro,
    },
    estatisticas: {
        flexDirection: "row",
        backgroundColor: cores.cartao,
        borderRadius: 14,
        paddingVertical: 20,
        marginBottom: 24,
    },
    estatItem: {
        flex: 1,
        alignItems: "center",
    },
    estatValor: {
        fontSize: 26,
        fontWeight: "800",
        color: cores.primaria,
    },
    estatLabel: {
        fontSize: 13,
        color: cores.textoClaro,
        marginTop: 4,
    },
    divisor: {
        width: 1,
        backgroundColor: cores.borda,
    },
    menu: {
        backgroundColor: cores.cartao,
        borderRadius: 14,
        overflow: "hidden",
    },
    itemMenu: {
        flexDirection: "row",
        alignItems: "center",
        paddingHorizontal: 16,
        paddingVertical: 16,
        borderBottomWidth: 1,
        borderBottomColor: cores.fundo,
    },
    itemTexto: {
        flex: 1,
        fontSize: 16,
        color: cores.texto,
        fontWeight: "600",
    },
    formCard: {
        backgroundColor: cores.cartao,
        borderRadius: 14,
        padding: 18,
    },
    label: {
        fontSize: 14,
        fontWeight: "600",
        color: cores.textoClaro,
        marginBottom: 6,
    },
    input: {
        height: 50,
        borderWidth: 1,
        borderColor: cores.borda,
        borderRadius: 12,
        paddingHorizontal: 15,
        fontSize: 16,
        color: cores.texto,
        backgroundColor: "#fbfbfd",
        marginBottom: 16,
    },
    formAcoes: {
        flexDirection: "row",
        gap: 12,
    },
    botao: {
        flex: 1,
        height: 48,
        borderRadius: 12,
        justifyContent: "center",
        alignItems: "center",
    },
    botaoSalvar: {
        backgroundColor: cores.destaque,
    },
    botaoSalvarTexto: {
        color: "#fff",
        fontWeight: "700",
    },
    botaoCancelar: {
        backgroundColor: cores.fundo,
    },
    botaoCancelarTexto: {
        color: cores.texto,
        fontWeight: "700",
    },
});

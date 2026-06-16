import { Ionicons } from "@expo/vector-icons";
import { useLocalSearchParams, useRouter } from "expo-router";
import { useCallback, useEffect, useState } from "react";
import {
    ActivityIndicator,
    Alert,
    Image,
    ScrollView,
    StyleSheet,
    Text,
    TouchableOpacity,
    View,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { enviarPalpite } from "../../services/palpiteService";
import { buscarPartidaPorId } from "../../services/partidaService";
import { cores } from "../../theme/cores";
import { Partida, Selecao } from "../../types/models";
import { mensagemDeErro } from "../../utils/erros";
import { formatarDataHora, rotuloStatus } from "../../utils/formato";

export default function PartidaDetalheScreen() {
    const { id } = useLocalSearchParams<{ id: string }>();
    const router = useRouter();

    const [partida, setPartida] = useState<Partida | null>(null);
    const [carregando, setCarregando] = useState(true);
    const [golsMandante, setGolsMandante] = useState(0);
    const [golsVisitante, setGolsVisitante] = useState(0);
    const [enviando, setEnviando] = useState(false);

    const carregar = useCallback(async () => {
        try {
            setCarregando(true);
            const dados = await buscarPartidaPorId(Number(id));
            setPartida(dados);
            if (dados.golsMandante !== null) setGolsMandante(dados.golsMandante);
            if (dados.golsVisitante !== null) setGolsVisitante(dados.golsVisitante);
        } catch (erro) {
            Alert.alert("Atencao!", mensagemDeErro(erro));
        } finally {
            setCarregando(false);
        }
    }, [id]);

    useEffect(() => {
        carregar();
    }, [carregar]);

    async function palpitar() {
        try {
            setEnviando(true);
            await enviarPalpite(Number(id), golsMandante, golsVisitante);
            Alert.alert("Palpite registrado!", "Boa sorte! Voce pode editar ate o inicio da partida.", [
                { text: "Ver meus palpites", onPress: () => router.replace("/(tabs)/palpites") },
                { text: "OK", style: "cancel" },
            ]);
        } catch (erro) {
            // Inclui o 422 de bloqueio apos o inicio da partida (regra 4.2).
            Alert.alert("Nao foi possivel palpitar", mensagemDeErro(erro));
        } finally {
            setEnviando(false);
        }
    }

    if (carregando || !partida) {
        return (
            <SafeAreaView style={styles.center}>
                <ActivityIndicator size="large" color={cores.primaria} />
            </SafeAreaView>
        );
    }

    const aberta = partida.abertaParaPalpite;

    return (
        <SafeAreaView style={styles.container} edges={["top", "left", "right"]}>
            <View style={styles.header}>
                <TouchableOpacity onPress={() => router.back()}>
                    <Ionicons name="arrow-back" size={26} color="#fff" />
                </TouchableOpacity>
                <Text style={styles.headerTitulo}>{partida.faseDescricao}</Text>
                <View style={{ width: 26 }} />
            </View>

            <ScrollView contentContainerStyle={styles.scroll}>
                <View style={styles.placarCard}>
                    <Time selecao={partida.mandante} />

                    <View style={styles.placarCentro}>
                        {partida.golsMandante !== null && partida.golsVisitante !== null ? (
                            <Text style={styles.placar}>
                                {partida.golsMandante} - {partida.golsVisitante}
                            </Text>
                        ) : (
                            <Text style={styles.versus}>VS</Text>
                        )}
                        <View style={styles.statusTag}>
                            <Text style={styles.statusTexto}>{rotuloStatus(partida.status)}</Text>
                        </View>
                    </View>

                    <Time selecao={partida.visitante} />
                </View>

                <View style={styles.infoCard}>
                    <InfoLinha icone="calendar-outline" rotulo="Data e hora"
                        valor={formatarDataHora(partida.dataHora)} />
                    <InfoLinha icone="location-outline" rotulo="Estadio"
                        valor={partida.estadio ?? "A definir"} />
                    {partida.grupo && (
                        <InfoLinha icone="people-outline" rotulo="Grupo" valor={partida.grupo} />
                    )}
                </View>

                {/* Area de palpite: so habilitada se a partida ainda nao iniciou (RF-020/RF-022). */}
                <Text style={styles.secaoTitulo}>Seu palpite</Text>

                {aberta ? (
                    <View style={styles.palpiteCard}>
                        <View style={styles.palpiteLinha}>
                            <Contador
                                titulo={partida.mandante.codigoFifa}
                                valor={golsMandante}
                                onMudar={setGolsMandante}
                            />
                            <Text style={styles.x}>x</Text>
                            <Contador
                                titulo={partida.visitante.codigoFifa}
                                valor={golsVisitante}
                                onMudar={setGolsVisitante}
                            />
                        </View>

                        <TouchableOpacity
                            style={[styles.botaoPalpitar, enviando && { opacity: 0.6 }]}
                            onPress={palpitar}
                            disabled={enviando}
                        >
                            <Text style={styles.botaoPalpitarTexto}>
                                {enviando ? "Enviando..." : "Confirmar palpite"}
                            </Text>
                        </TouchableOpacity>
                    </View>
                ) : (
                    <View style={styles.fechado}>
                        <Ionicons name="lock-closed-outline" size={22} color={cores.textoClaro} />
                        <Text style={styles.fechadoTexto}>
                            Os palpites para esta partida estao encerrados.
                        </Text>
                    </View>
                )}
            </ScrollView>
        </SafeAreaView>
    );
}

function Time({ selecao }: { selecao: Selecao }) {
    return (
        <View style={styles.time}>
            {selecao.bandeiraUrl ? (
                <Image source={{ uri: selecao.bandeiraUrl }} style={styles.bandeira} />
            ) : (
                <View style={[styles.bandeira, styles.bandeiraVazia]} />
            )}
            <Text style={styles.timeNome}>{selecao.codigoFifa}</Text>
        </View>
    );
}

function InfoLinha({
    icone,
    rotulo,
    valor,
}: {
    icone: keyof typeof Ionicons.glyphMap;
    rotulo: string;
    valor: string;
}) {
    return (
        <View style={styles.infoLinha}>
            <Ionicons name={icone} size={20} color={cores.primaria} style={{ width: 30 }} />
            <View>
                <Text style={styles.infoRotulo}>{rotulo}</Text>
                <Text style={styles.infoValor}>{valor}</Text>
            </View>
        </View>
    );
}

function Contador({
    titulo,
    valor,
    onMudar,
}: {
    titulo: string;
    valor: number;
    onMudar: (v: number) => void;
}) {
    return (
        <View style={styles.contador}>
            <Text style={styles.contadorTitulo}>{titulo}</Text>
            <View style={styles.contadorControles}>
                <TouchableOpacity
                    style={styles.contadorBotao}
                    onPress={() => onMudar(Math.max(0, valor - 1))}
                >
                    <Ionicons name="remove" size={22} color={cores.primaria} />
                </TouchableOpacity>
                <Text style={styles.contadorValor}>{valor}</Text>
                <TouchableOpacity style={styles.contadorBotao} onPress={() => onMudar(valor + 1)}>
                    <Ionicons name="add" size={22} color={cores.primaria} />
                </TouchableOpacity>
            </View>
        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: cores.fundo,
    },
    center: {
        flex: 1,
        justifyContent: "center",
        alignItems: "center",
        backgroundColor: cores.fundo,
    },
    header: {
        flexDirection: "row",
        alignItems: "center",
        justifyContent: "space-between",
        backgroundColor: cores.primaria,
        paddingHorizontal: 16,
        paddingVertical: 14,
    },
    headerTitulo: {
        color: "#fff",
        fontSize: 16,
        fontWeight: "700",
    },
    scroll: {
        padding: 20,
    },
    placarCard: {
        flexDirection: "row",
        backgroundColor: cores.primaria,
        borderRadius: 16,
        padding: 20,
        alignItems: "center",
        justifyContent: "space-between",
    },
    time: {
        alignItems: "center",
        width: 90,
    },
    bandeira: {
        width: 60,
        height: 40,
        borderRadius: 5,
        marginBottom: 8,
    },
    bandeiraVazia: {
        backgroundColor: "rgba(255,255,255,0.2)",
    },
    timeNome: {
        color: "#fff",
        fontSize: 18,
        fontWeight: "800",
    },
    placarCentro: {
        alignItems: "center",
    },
    placar: {
        color: "#fff",
        fontSize: 32,
        fontWeight: "800",
    },
    versus: {
        color: cores.dourado,
        fontSize: 22,
        fontWeight: "800",
    },
    statusTag: {
        marginTop: 8,
        backgroundColor: "rgba(255,255,255,0.15)",
        paddingHorizontal: 12,
        paddingVertical: 4,
        borderRadius: 20,
    },
    statusTexto: {
        color: "#fff",
        fontSize: 11,
        fontWeight: "700",
    },
    infoCard: {
        backgroundColor: cores.cartao,
        borderRadius: 14,
        padding: 16,
        marginTop: 16,
    },
    infoLinha: {
        flexDirection: "row",
        alignItems: "center",
        paddingVertical: 8,
    },
    infoRotulo: {
        fontSize: 12,
        color: cores.textoClaro,
    },
    infoValor: {
        fontSize: 15,
        color: cores.texto,
        fontWeight: "600",
    },
    secaoTitulo: {
        fontSize: 18,
        fontWeight: "800",
        color: cores.texto,
        marginTop: 24,
        marginBottom: 12,
    },
    palpiteCard: {
        backgroundColor: cores.cartao,
        borderRadius: 14,
        padding: 20,
    },
    palpiteLinha: {
        flexDirection: "row",
        alignItems: "center",
        justifyContent: "space-between",
    },
    x: {
        fontSize: 22,
        fontWeight: "800",
        color: cores.textoClaro,
    },
    contador: {
        alignItems: "center",
    },
    contadorTitulo: {
        fontSize: 16,
        fontWeight: "700",
        color: cores.texto,
        marginBottom: 10,
    },
    contadorControles: {
        flexDirection: "row",
        alignItems: "center",
    },
    contadorBotao: {
        width: 40,
        height: 40,
        borderRadius: 20,
        backgroundColor: cores.fundo,
        justifyContent: "center",
        alignItems: "center",
    },
    contadorValor: {
        fontSize: 28,
        fontWeight: "800",
        color: cores.primaria,
        marginHorizontal: 18,
        minWidth: 34,
        textAlign: "center",
    },
    botaoPalpitar: {
        backgroundColor: cores.destaque,
        height: 52,
        borderRadius: 12,
        justifyContent: "center",
        alignItems: "center",
        marginTop: 24,
    },
    botaoPalpitarTexto: {
        color: "#fff",
        fontWeight: "800",
        fontSize: 16,
    },
    fechado: {
        backgroundColor: cores.cartao,
        borderRadius: 14,
        padding: 20,
        alignItems: "center",
    },
    fechadoTexto: {
        color: cores.textoClaro,
        marginTop: 8,
        textAlign: "center",
    },
});

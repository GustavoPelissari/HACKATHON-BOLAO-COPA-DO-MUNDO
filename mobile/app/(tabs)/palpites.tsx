import { useFocusEffect } from "expo-router";
import { useCallback, useState } from "react";
import {
    ActivityIndicator,
    FlatList,
    RefreshControl,
    StyleSheet,
    Text,
    View,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import EstadoVazio from "../../componentes/EstadoVazio";
import { buscarMeusPalpites } from "../../services/palpiteService";
import { cores } from "../../theme/cores";
import { Palpite } from "../../types/models";
import { formatarDataHora } from "../../utils/formato";

export default function PalpitesScreen() {
    const [palpites, setPalpites] = useState<Palpite[]>([]);
    const [carregando, setCarregando] = useState(false);

    const carregar = useCallback(async () => {
        try {
            setCarregando(true);
            setPalpites(await buscarMeusPalpites());
        } finally {
            setCarregando(false);
        }
    }, []);

    useFocusEffect(
        useCallback(() => {
            carregar();
        }, [carregar])
    );

    return (
        <SafeAreaView style={styles.container} edges={["top", "left", "right"]}>
            <View style={styles.header}>
                <Text style={styles.headerTitulo}>Meus palpites</Text>
            </View>

            {carregando && palpites.length === 0 ? (
                <ActivityIndicator size="large" color={cores.primaria} style={{ marginTop: 40 }} />
            ) : (
                <FlatList
                    data={palpites}
                    keyExtractor={(item) => String(item.id)}
                    renderItem={({ item }) => <PalpiteLinha palpite={item} />}
                    contentContainerStyle={styles.lista}
                    showsVerticalScrollIndicator={false}
                    refreshControl={<RefreshControl refreshing={carregando} onRefresh={carregar} />}
                    ListEmptyComponent={
                        <EstadoVazio
                            icone="pencil-square-o"
                            titulo="Nenhum palpite ainda"
                            descricao="Abra uma partida em Partidas e registre seu palpite."
                        />
                    }
                />
            )}
        </SafeAreaView>
    );
}

function PalpiteLinha({ palpite }: { palpite: Palpite }) {
    const { partida } = palpite;
    const encerrada = partida.status === "ENCERRADA";

    return (
        <View style={styles.card}>
            <View style={styles.cardTopo}>
                <Text style={styles.fase}>{partida.faseDescricao}</Text>
                <Text style={styles.data}>{formatarDataHora(partida.dataHora)}</Text>
            </View>

            <View style={styles.linha}>
                <Text style={styles.time}>{partida.mandante.codigoFifa}</Text>
                <Text style={styles.meuPlacar}>
                    {palpite.golsMandante} x {palpite.golsVisitante}
                </Text>
                <Text style={styles.time}>{partida.visitante.codigoFifa}</Text>
            </View>

            {encerrada ? (
                <View style={styles.resultadoBox}>
                    <Text style={styles.resultadoTexto}>
                        Resultado: {partida.golsMandante} x {partida.golsVisitante}
                    </Text>
                    <View
                        style={[
                            styles.pontos,
                            (palpite.pontosObtidos ?? 0) > 0 ? styles.pontosOk : styles.pontosZero,
                        ]}
                    >
                        <Text style={styles.pontosTexto}>+{palpite.pontosObtidos ?? 0} pts</Text>
                    </View>
                </View>
            ) : (
                <Text style={styles.aguardando}>Aguardando o resultado</Text>
            )}

            {encerrada && palpite.criterioAplicado && (
                <Text style={styles.criterio}>{palpite.criterioAplicado}</Text>
            )}
        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: cores.fundo,
    },
    header: {
        paddingHorizontal: 20,
        paddingTop: 8,
        paddingBottom: 12,
    },
    headerTitulo: {
        fontSize: 28,
        fontWeight: "800",
        color: cores.primaria,
    },
    lista: {
        paddingHorizontal: 20,
        paddingBottom: 30,
    },
    card: {
        backgroundColor: cores.cartao,
        borderRadius: 14,
        padding: 16,
        marginBottom: 14,
        elevation: 2,
        shadowColor: "#000",
        shadowOffset: { width: 0, height: 1 },
        shadowOpacity: 0.08,
        shadowRadius: 4,
    },
    cardTopo: {
        flexDirection: "row",
        justifyContent: "space-between",
        marginBottom: 12,
    },
    fase: {
        fontSize: 12,
        fontWeight: "700",
        color: cores.textoClaro,
        textTransform: "uppercase",
    },
    data: {
        fontSize: 12,
        color: cores.textoClaro,
    },
    linha: {
        flexDirection: "row",
        alignItems: "center",
        justifyContent: "space-between",
    },
    time: {
        fontSize: 16,
        fontWeight: "700",
        color: cores.texto,
        width: 60,
        textAlign: "center",
    },
    meuPlacar: {
        fontSize: 22,
        fontWeight: "800",
        color: cores.primaria,
    },
    resultadoBox: {
        flexDirection: "row",
        justifyContent: "space-between",
        alignItems: "center",
        marginTop: 14,
        borderTopWidth: 1,
        borderTopColor: cores.borda,
        paddingTop: 12,
    },
    resultadoTexto: {
        fontSize: 13,
        color: cores.textoClaro,
    },
    pontos: {
        paddingHorizontal: 12,
        paddingVertical: 4,
        borderRadius: 20,
    },
    pontosOk: {
        backgroundColor: "#e8f5e9",
    },
    pontosZero: {
        backgroundColor: "#fdecea",
    },
    pontosTexto: {
        fontWeight: "800",
        fontSize: 13,
        color: cores.texto,
    },
    aguardando: {
        marginTop: 12,
        fontSize: 13,
        color: cores.textoClaro,
        fontStyle: "italic",
        textAlign: "center",
    },
    criterio: {
        marginTop: 8,
        fontSize: 12,
        color: cores.textoClaro,
        textAlign: "center",
    },
});

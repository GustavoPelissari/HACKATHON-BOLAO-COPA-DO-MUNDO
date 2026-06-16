import { useFocusEffect } from "expo-router";
import { useCallback, useState } from "react";
import {
    ActivityIndicator,
    FlatList,
    Image,
    RefreshControl,
    StyleSheet,
    Text,
    View,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import EstadoVazio from "../../componentes/EstadoVazio";
import { buscarRanking } from "../../services/rankingService";
import { cores } from "../../theme/cores";
import { RankingItem } from "../../types/models";

export default function RankingScreen() {
    const [itens, setItens] = useState<RankingItem[]>([]);
    const [minhaPosicao, setMinhaPosicao] = useState<RankingItem | null>(null);
    const [totalUsuarios, setTotalUsuarios] = useState(0);
    const [carregando, setCarregando] = useState(false);

    const carregar = useCallback(async () => {
        try {
            setCarregando(true);
            const pagina = await buscarRanking(0);
            setItens(pagina.itens);
            setMinhaPosicao(pagina.minhaPosicao);
            setTotalUsuarios(pagina.totalUsuarios);
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
                <Text style={styles.headerTitulo}>Ranking geral</Text>
                <Text style={styles.headerSub}>{totalUsuarios} participantes</Text>
            </View>

            {carregando && itens.length === 0 ? (
                <ActivityIndicator size="large" color={cores.primaria} style={{ marginTop: 40 }} />
            ) : (
                <FlatList
                    data={itens}
                    keyExtractor={(item) => String(item.usuarioId)}
                    renderItem={({ item }) => <RankingLinha item={item} />}
                    contentContainerStyle={styles.lista}
                    showsVerticalScrollIndicator={false}
                    refreshControl={<RefreshControl refreshing={carregando} onRefresh={carregar} />}
                    ListEmptyComponent={
                        <EstadoVazio icone="trophy" titulo="Ranking vazio" descricao="Seja o primeiro a pontuar!" />
                    }
                />
            )}

            {/* RF-033: posicao do usuario destacada de forma fixa no rodape. */}
            {minhaPosicao && (
                <View style={styles.rodapeFixo}>
                    <RankingLinha item={minhaPosicao} fixo />
                </View>
            )}
        </SafeAreaView>
    );
}

function RankingLinha({ item, fixo = false }: { item: RankingItem; fixo?: boolean }) {
    const destaque = item.ehUsuarioAtual && !fixo;
    const medalha = item.posicao <= 3;

    return (
        <View style={[styles.linha, destaque && styles.linhaDestaque, fixo && styles.linhaFixa]}>
            <View style={[styles.posicaoBox, medalha && styles.posicaoMedalha]}>
                <Text style={[styles.posicao, medalha && styles.posicaoMedalhaTexto]}>
                    {item.posicao}
                </Text>
            </View>

            {item.avatarUrl ? (
                <Image source={{ uri: item.avatarUrl }} style={styles.avatar} />
            ) : (
                <View style={[styles.avatar, styles.avatarVazio]}>
                    <Text style={styles.avatarInicial}>{item.nome.charAt(0).toUpperCase()}</Text>
                </View>
            )}

            <View style={styles.info}>
                <Text style={[styles.nome, fixo && styles.nomeFixo]} numberOfLines={1}>
                    {item.nome} {item.ehUsuarioAtual && "(voce)"}
                </Text>
                <Text style={styles.exatos}>{item.placaresExatos} placares exatos</Text>
            </View>

            <Text style={[styles.pontos, fixo && styles.pontosFixo]}>{item.pontuacaoTotal}</Text>
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
    headerSub: {
        fontSize: 14,
        color: cores.textoClaro,
    },
    lista: {
        paddingHorizontal: 20,
        paddingBottom: 90,
    },
    linha: {
        flexDirection: "row",
        alignItems: "center",
        backgroundColor: cores.cartao,
        borderRadius: 12,
        padding: 12,
        marginBottom: 8,
    },
    linhaDestaque: {
        borderWidth: 2,
        borderColor: cores.destaque,
    },
    linhaFixa: {
        marginBottom: 0,
        backgroundColor: cores.primaria,
    },
    posicaoBox: {
        width: 34,
        height: 34,
        borderRadius: 17,
        backgroundColor: cores.fundo,
        justifyContent: "center",
        alignItems: "center",
    },
    posicaoMedalha: {
        backgroundColor: cores.dourado,
    },
    posicao: {
        fontWeight: "800",
        color: cores.texto,
    },
    posicaoMedalhaTexto: {
        color: cores.primaria,
    },
    avatar: {
        width: 40,
        height: 40,
        borderRadius: 20,
        marginHorizontal: 12,
    },
    avatarVazio: {
        backgroundColor: cores.primaria,
        justifyContent: "center",
        alignItems: "center",
    },
    avatarInicial: {
        color: "#fff",
        fontWeight: "800",
        fontSize: 16,
    },
    info: {
        flex: 1,
    },
    nome: {
        fontSize: 15,
        fontWeight: "700",
        color: cores.texto,
    },
    nomeFixo: {
        color: "#fff",
    },
    exatos: {
        fontSize: 12,
        color: cores.textoClaro,
    },
    pontos: {
        fontSize: 20,
        fontWeight: "800",
        color: cores.primaria,
    },
    pontosFixo: {
        color: cores.dourado,
    },
    rodapeFixo: {
        position: "absolute",
        bottom: 0,
        left: 0,
        right: 0,
        padding: 16,
        backgroundColor: cores.fundo,
        borderTopWidth: 1,
        borderTopColor: cores.borda,
    },
});

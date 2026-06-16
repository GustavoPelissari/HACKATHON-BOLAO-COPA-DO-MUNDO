import { useFocusEffect, useRouter } from "expo-router";
import { useCallback, useState } from "react";
import {
    ActivityIndicator,
    FlatList,
    RefreshControl,
    ScrollView,
    StyleSheet,
    Text,
    TouchableOpacity,
    View,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import PartidaCard from "../../componentes/PartidaCard";
import EstadoVazio from "../../componentes/EstadoVazio";
import { buscarPartidas } from "../../services/partidaService";
import { cores } from "../../theme/cores";
import { Partida } from "../../types/models";

// Filtros por fase (RF-012). "TODAS" remove o filtro.
const FILTROS = [
    { rotulo: "Todas", valor: undefined },
    { rotulo: "Grupos", valor: "GRUPOS" },
    { rotulo: "16-avos", valor: "DEZESSEIS_AVOS" },
    { rotulo: "Oitavas", valor: "OITAVAS" },
    { rotulo: "Quartas", valor: "QUARTAS" },
    { rotulo: "Semi", valor: "SEMI" },
    { rotulo: "3o lugar", valor: "TERCEIRO_LUGAR" },
    { rotulo: "Final", valor: "FINAL" },
] as const;

export default function PartidasScreen() {
    const router = useRouter();
    const [partidas, setPartidas] = useState<Partida[]>([]);
    const [carregando, setCarregando] = useState(false);
    const [faseSelecionada, setFaseSelecionada] = useState<string | undefined>(undefined);

    const carregar = useCallback(async (fase?: string) => {
        try {
            setCarregando(true);
            const dados = await buscarPartidas({ fase });
            setPartidas(dados);
        } finally {
            setCarregando(false);
        }
    }, []);

    // Recarrega sempre que a aba ganha foco.
    useFocusEffect(
        useCallback(() => {
            carregar(faseSelecionada);
        }, [carregar, faseSelecionada])
    );

    function selecionarFase(fase?: string) {
        setFaseSelecionada(fase);
        carregar(fase);
    }

    return (
        <SafeAreaView style={styles.container} edges={["top", "left", "right"]}>
            <View style={styles.header}>
                <Text style={styles.headerTitulo}>Partidas</Text>
                <Text style={styles.headerSub}>Copa do Mundo FIFA 2026</Text>
            </View>

            <View style={styles.filtros}>
                <ScrollView horizontal showsHorizontalScrollIndicator={false}>
                    {FILTROS.map((filtro) => {
                        const ativo = filtro.valor === faseSelecionada;
                        return (
                            <TouchableOpacity
                                key={filtro.rotulo}
                                style={[styles.chip, ativo && styles.chipAtivo]}
                                onPress={() => selecionarFase(filtro.valor)}
                            >
                                <Text style={[styles.chipTexto, ativo && styles.chipTextoAtivo]}>
                                    {filtro.rotulo}
                                </Text>
                            </TouchableOpacity>
                        );
                    })}
                </ScrollView>
            </View>

            {carregando && partidas.length === 0 ? (
                <ActivityIndicator size="large" color={cores.primaria} style={{ marginTop: 40 }} />
            ) : (
                <FlatList
                    data={partidas}
                    keyExtractor={(item) => String(item.id)}
                    renderItem={({ item }) => (
                        <PartidaCard partida={item} onPress={() => router.push(`/partida/${item.id}`)} />
                    )}
                    contentContainerStyle={styles.lista}
                    showsVerticalScrollIndicator={false}
                    refreshControl={
                        <RefreshControl refreshing={carregando} onRefresh={() => carregar(faseSelecionada)} />
                    }
                    ListEmptyComponent={
                        <EstadoVazio
                            icone="calendar-o"
                            titulo="Nenhuma partida"
                            descricao="Ainda nao ha partidas nesta fase."
                        />
                    }
                />
            )}
        </SafeAreaView>
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
    filtros: {
        paddingLeft: 20,
        paddingBottom: 12,
    },
    chip: {
        paddingHorizontal: 16,
        paddingVertical: 8,
        backgroundColor: cores.cartao,
        borderRadius: 20,
        marginRight: 8,
        borderWidth: 1,
        borderColor: cores.borda,
    },
    chipAtivo: {
        backgroundColor: cores.primaria,
        borderColor: cores.primaria,
    },
    chipTexto: {
        fontSize: 14,
        fontWeight: "600",
        color: cores.texto,
    },
    chipTextoAtivo: {
        color: "#fff",
    },
    lista: {
        paddingHorizontal: 20,
        paddingBottom: 30,
    },
});

import { Image, StyleSheet, Text, TouchableOpacity, View } from "react-native";
import { cores } from "../theme/cores";
import { Partida } from "../types/models";
import { formatarDataHora, rotuloStatus } from "../utils/formato";

type PartidaCardProps = {
    partida: Partida;
    onPress: () => void;
};

/** Cartao que resume uma partida na lista (RF-010). */
export default function PartidaCard({ partida, onPress }: PartidaCardProps) {
    const encerrada = partida.status === "ENCERRADA";

    return (
        <TouchableOpacity style={styles.card} onPress={onPress} activeOpacity={0.85}>
            <View style={styles.topo}>
                <Text style={styles.fase}>{partida.faseDescricao}</Text>
                <View style={[styles.statusTag, encerrada && styles.statusEncerrada]}>
                    <Text style={[styles.statusTexto, encerrada && styles.statusTextoEncerrada]}>
                        {rotuloStatus(partida.status)}
                    </Text>
                </View>
            </View>

            <View style={styles.confronto}>
                <Time
                    nome={partida.mandante.codigoFifa}
                    bandeira={partida.mandante.bandeiraUrl}
                />

                <View style={styles.centro}>
                    {partida.golsMandante !== null && partida.golsVisitante !== null ? (
                        <Text style={styles.placar}>
                            {partida.golsMandante} - {partida.golsVisitante}
                        </Text>
                    ) : (
                        <Text style={styles.versus}>VS</Text>
                    )}
                    <Text style={styles.data}>{formatarDataHora(partida.dataHora)}</Text>
                </View>

                <Time
                    nome={partida.visitante.codigoFifa}
                    bandeira={partida.visitante.bandeiraUrl}
                />
            </View>

            {partida.abertaParaPalpite && (
                <View style={styles.aberta}>
                    <Text style={styles.abertaTexto}>Aberta para palpite</Text>
                </View>
            )}
        </TouchableOpacity>
    );
}

function Time({ nome, bandeira }: { nome: string; bandeira: string | null }) {
    return (
        <View style={styles.time}>
            {bandeira ? (
                <Image source={{ uri: bandeira }} style={styles.bandeira} />
            ) : (
                <View style={[styles.bandeira, styles.bandeiraVazia]} />
            )}
            <Text style={styles.timeNome}>{nome}</Text>
        </View>
    );
}

const styles = StyleSheet.create({
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
    topo: {
        flexDirection: "row",
        justifyContent: "space-between",
        alignItems: "center",
        marginBottom: 12,
    },
    fase: {
        fontSize: 12,
        fontWeight: "700",
        color: cores.textoClaro,
        textTransform: "uppercase",
    },
    statusTag: {
        backgroundColor: "#eef6ff",
        paddingHorizontal: 10,
        paddingVertical: 3,
        borderRadius: 20,
    },
    statusEncerrada: {
        backgroundColor: "#e8f5e9",
    },
    statusTexto: {
        fontSize: 11,
        fontWeight: "700",
        color: cores.primaria,
    },
    statusTextoEncerrada: {
        color: cores.sucesso,
    },
    confronto: {
        flexDirection: "row",
        alignItems: "center",
        justifyContent: "space-between",
    },
    time: {
        alignItems: "center",
        width: 90,
    },
    bandeira: {
        width: 48,
        height: 32,
        borderRadius: 4,
        marginBottom: 6,
    },
    bandeiraVazia: {
        backgroundColor: cores.borda,
    },
    timeNome: {
        fontSize: 15,
        fontWeight: "700",
        color: cores.texto,
    },
    centro: {
        alignItems: "center",
        flex: 1,
    },
    placar: {
        fontSize: 24,
        fontWeight: "800",
        color: cores.texto,
    },
    versus: {
        fontSize: 18,
        fontWeight: "800",
        color: cores.textoClaro,
    },
    data: {
        fontSize: 12,
        color: cores.textoClaro,
        marginTop: 4,
    },
    aberta: {
        marginTop: 12,
        backgroundColor: "#e8f5e9",
        borderRadius: 8,
        paddingVertical: 5,
        alignItems: "center",
    },
    abertaTexto: {
        fontSize: 12,
        fontWeight: "700",
        color: cores.destaque,
    },
});

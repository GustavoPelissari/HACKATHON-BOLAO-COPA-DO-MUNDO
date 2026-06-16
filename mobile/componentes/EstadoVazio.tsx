import { FontAwesome } from "@expo/vector-icons";
import { StyleSheet, Text, View } from "react-native";
import { cores } from "../theme/cores";

type EstadoVazioProps = {
    icone?: keyof typeof FontAwesome.glyphMap;
    titulo: string;
    descricao?: string;
};

/** Placeholder exibido quando uma lista esta vazia. */
export default function EstadoVazio({ icone = "inbox", titulo, descricao }: EstadoVazioProps) {
    return (
        <View style={styles.container}>
            <FontAwesome name={icone} size={48} color={cores.borda} />
            <Text style={styles.titulo}>{titulo}</Text>
            {descricao && <Text style={styles.descricao}>{descricao}</Text>}
        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        alignItems: "center",
        justifyContent: "center",
        paddingVertical: 60,
        paddingHorizontal: 30,
    },
    titulo: {
        fontSize: 16,
        fontWeight: "700",
        color: cores.texto,
        marginTop: 16,
        textAlign: "center",
    },
    descricao: {
        fontSize: 14,
        color: cores.textoClaro,
        marginTop: 6,
        textAlign: "center",
    },
});

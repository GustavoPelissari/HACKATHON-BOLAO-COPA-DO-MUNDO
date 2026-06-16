import { Redirect } from "expo-router";
import { ActivityIndicator, StyleSheet, View } from "react-native";
import { useAuth } from "../contexts/AuthContext";
import { cores } from "../theme/cores";

/**
 * Porta de entrada: enquanto restaura a sessao mostra um loading; depois
 * redireciona para as abas (autenticado) ou para o login.
 */
export default function Index() {
    const { carregando, autenticado } = useAuth();

    if (carregando) {
        return (
            <View style={styles.container}>
                <ActivityIndicator size="large" color={cores.primaria} />
            </View>
        );
    }

    return autenticado ? <Redirect href="/(tabs)/partidas" /> : <Redirect href="/login" />;
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: "center",
        alignItems: "center",
        backgroundColor: cores.fundo,
    },
});

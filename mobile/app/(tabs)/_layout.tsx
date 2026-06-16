import FontAwesome from "@expo/vector-icons/FontAwesome";
import { Tabs } from "expo-router";
import { cores } from "../../theme/cores";

export default function TabLayout() {
    return (
        <Tabs
            screenOptions={{
                tabBarActiveTintColor: cores.primaria,
                tabBarInactiveTintColor: cores.textoClaro,
                headerShown: false,
            }}
        >
            <Tabs.Screen
                name="partidas"
                options={{
                    title: "Partidas",
                    tabBarIcon: ({ color }) => <FontAwesome name="futbol-o" size={22} color={color} />,
                }}
            />
            <Tabs.Screen
                name="palpites"
                options={{
                    title: "Palpites",
                    tabBarIcon: ({ color }) => <FontAwesome name="check-square-o" size={22} color={color} />,
                }}
            />
            <Tabs.Screen
                name="ranking"
                options={{
                    title: "Ranking",
                    tabBarIcon: ({ color }) => <FontAwesome name="trophy" size={22} color={color} />,
                }}
            />
            <Tabs.Screen
                name="perfil"
                options={{
                    title: "Perfil",
                    tabBarIcon: ({ color }) => <FontAwesome name="user" size={24} color={color} />,
                }}
            />
        </Tabs>
    );
}

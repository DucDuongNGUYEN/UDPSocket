import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class UDPSocket {
    private static boolean running = true;

    public static void main(String[] args) throws IOException {
        // Vérification des arguments
        if (args.length != 2) {
            System.out.println("Usage: java UDPSocket server port");
            return;
        }
        String server = args[0];
        int port = Integer.parseInt(args[1]);

        // Création d'un socket UDP
        DatagramSocket socket = new DatagramSocket();

        // Thread pour gérer la partie client
        Thread clientThread = new Thread(() -> {
            try {
                // Boucle de lecture de lignes au clavier
                Scanner in = new Scanner(System.in);
                while (running && in.hasNextLine()) {
                    String line = ">"+in.nextLine()+"\n";

                    // Conversion de la ligne en tableau d'octets
                    byte[] data = line.getBytes();

                    // Création d'un paquet avec les données à envoyer, l'adresse de destination et le port
                    InetAddress address = InetAddress.getByName(server);
                    DatagramPacket packet = new DatagramPacket(data, data.length, address, port);

                    // Envoi du paquet
                    socket.send(packet);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        clientThread.start();

        // Thread pour gérer la partie serveur
        Thread serverThread = new Thread(() -> {
            try {
                while (running) {
                    // Réception d'un paquet
                    byte[] buffer = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);

                    // Décodage de la ligne reçue
                    String line = new String(packet.getData());

                    // Préparation de la réponse
                    String response = ">" + line + "\n";
                    byte[] data = response.getBytes();

                    // Envoi de la réponse
                    packet.setData(data);
                    socket.send(packet);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        serverThread.start();

        // Attente de la fin des threads
        try {
            clientThread.join();
            serverThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Fermeture du socket
        //socket.close();
    }
}


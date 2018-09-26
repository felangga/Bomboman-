/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bomboman;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JOptionPane;

/**
 *
 * @author fcomputer
 */
public class BomboMain extends JLayeredPane {

    private int papan[][];
    private JFrame frame = new JFrame();
    private static JFrame window;
    private Timer timerPermainan = new Timer();
    private int detik = 300;
    private String waktuPermainan = "5:00";
    private Boolean isServer;
    private String ipTujuan;
    // Handle informasi pemain
    private Pemain player;
    private Pemain allPlayer[] = new Pemain[4];
    private JLabel tampilNyawa = new JLabel();
    private JLabel tampilBomb = new JLabel();
    private JLabel tampilScore = new JLabel();
    private JLabel tampilWaktu = new JLabel();
    private int bawaBomb = 5;
    private int urutan = 1;
    private int currentPemain = 1;
    private static ServerSocket server;
    private static String dataPemain[] = new String[4];                         // data pemain yang akan dibroadcast oleh server
    private boolean online = true;
    // Handling informasi bomb
    private Timer tBomb[] = new Timer[200];
    private Timer serverBomb[] = new Timer[400];
    private Bomb cBomb[] = new Bomb[200];
    private Bomb sBomb[] = new Bomb[200];
    private int counter = 0;
    private int scount = 0;
    private ArrayList<String> al = new ArrayList<String>();
    private String clientBomb = "";
    private int[] sudahMeledak = new int[200];
    private String sama = "";
    private String filter = "";
    private String posBomb = "";
    private int[] peledak = new int[4];

    public BomboMain(Boolean isServer, final String ipServer, final String namaPemain) {

        try {
            setFrame();
            this.ipTujuan = ipServer;
            this.isServer = isServer;
            for (int i = 0; i < 4; i++) {
                allPlayer[i] = new Pemain(i + 1, bawaBomb, papan) {
                    @Override
                    public void tampil_bomb(Component b, String[] listBomb) {
                        // kosongi sudah dihandle dibawah
                    }
                };
            }

            if (isServer) {
                setUp(1);
                server = new ServerSocket(6666);
                Thread serverisasi = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("BomboMan v.1.0 - fcomputer.\nServer online");
                        while (online) {
                            try {
                                // handle jika ada koneksi baru
                                final Socket client = server.accept();
                                System.out.println("New connection to " + client.getInetAddress() + ", creating new game thread ...");

                                Thread sambungan = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Thread handleInput = new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        int counter = 0;
                                                        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                                                        final PrintWriter out = new PrintWriter(client.getOutputStream());
                                                        while (online) {
                                                            String input = in.readLine();

                                                            if (input.contains("RESET")) {
                                                                int pemain = Integer.parseInt(input.substring(5));
                                                                peledak[pemain - 1] = 0;
                                                            }
                                                            if (input.contains("HIT")) {
                                                                final int pemain = Integer.parseInt(input.substring(3));
                                                                System.out.println("HIT" + pemain);
                                                                switch (pemain) {
                                                                    case 1:
                                                                        player.setScore(player.getScore() + 500);
                                                                        break;
                                                                    default:
                                                                        peledak[pemain - 1] += 1;
                                                                        break;
                                                                }
                                                            }

                                                            if (input.contains("MATI")) {
                                                                int pemain = Integer.parseInt(input.substring(4));
                                                                // bunuh pemain
                                                                allPlayer[pemain - 1].setHealth(0);
                                                            }

                                                            if (input.contains("Player1=")) {
                                                                if (!allPlayer[0].getComponent().isValid()) {
                                                                    add(allPlayer[0].tampilkan(), new Integer(3), 0);
                                                                }
                                                                String potong[] = input.substring(8).split(",");
                                                                allPlayer[0].setMatrixX(Integer.parseInt(potong[0]));
                                                                allPlayer[0].setMatrixY(Integer.parseInt(potong[1]));
                                                                allPlayer[0].setHealth(Integer.parseInt(potong[2]));
                                                            }
                                                            if (input.contains("Player2=")) {
                                                                if (!allPlayer[1].getComponent().isValid()) {
                                                                    add(allPlayer[1].tampilkan(), new Integer(3), 0);
                                                                }

                                                                String potong[] = input.substring(8).split(",");
                                                                allPlayer[1].setMatrixX(Integer.parseInt(potong[0]));
                                                                allPlayer[1].setMatrixY(Integer.parseInt(potong[1]));
                                                                allPlayer[1].setHealth(Integer.parseInt(potong[2]));
                                                            }
                                                            if (input.contains("Player3=")) {
                                                                if (!allPlayer[2].getComponent().isValid()) {
                                                                    add(allPlayer[2].tampilkan(), new Integer(3), 0);
                                                                }
                                                                String potong[] = input.substring(8).split(",");
                                                                allPlayer[2].setMatrixX(Integer.parseInt(potong[0]));
                                                                allPlayer[2].setMatrixY(Integer.parseInt(potong[1]));
                                                                allPlayer[2].setHealth(Integer.parseInt(potong[2]));
                                                            }
                                                            if (input.contains("Player4=")) {
                                                                if (!allPlayer[3].getComponent().isValid()) {
                                                                    add(allPlayer[3].tampilkan(), new Integer(3), 0);
                                                                }
                                                                String potong[] = input.substring(8).split(",");
                                                                allPlayer[3].setMatrixX(Integer.parseInt(potong[0]));
                                                                allPlayer[3].setMatrixY(Integer.parseInt(potong[1]));
                                                                allPlayer[3].setHealth(Integer.parseInt(potong[2]));
                                                            }

                                                            if (input.contains("SKORKU")) {
                                                                int pemain = Integer.parseInt(input.substring(6, 7));
                                                                int skor = Integer.parseInt(input.substring(8));
                                                               
                                                                allPlayer[0].setScore(player.getScore());
                                                                allPlayer[pemain].setScore(skor);
                                                                
                                                                counter++;
                                                                if (counter >= currentPemain - 1) {
                                                                    online = false;
                                                                    System.out.println(counter + " " + (currentPemain-1));
                                                                } 
                                                            }

                                                            // Jika ada input lokasi bomb dari user lain
                                                            if (input.contains("B=") && input.length() > 2 && !filter.equalsIgnoreCase(input)) {
                                                                filter = input;

                                                                if (input.length() == 2) {
                                                                    scount = 0;
                                                                    clientBomb = "";
                                                                }
                                                                posBomb += input.substring(2);
                                                                String potong[] = input.substring(2).split(",");
                                                                final Animasi bn[] = new Animasi[4];
                                                                for (int i = 0; i < potong.length; i++) {
                                                                    String xy[] = potong[i].split("-");

                                                                    if (!xy[0].isEmpty() && !xy[1].isEmpty() && !clientBomb.contains(xy[0] + xy[1] + xy[2])) {
                                                                        //try {
                                                                        System.out.println("Count : " + scount);
                                                                        serverBomb[scount] = new Timer();
                                                                        clientBomb += xy[0] + xy[1] + xy[2] + ",";

                                                                        final int pelaku = Integer.parseInt(xy[2]);
                                                                        sBomb[scount] = new Bomb(pelaku, scount, Integer.parseInt(xy[0]), Integer.parseInt(xy[1]), serverBomb[scount]) {
                                                                            @Override
                                                                            public void meledak(int noBomb, int X, int Y, Timer sumberTimer) {
                                                                                sBomb[noBomb].setVisibility(false);

                                                                                // Hapus bomb dari list
                                                                                if (clientBomb.contains(Integer.toString(X) + Integer.toString(Y) + Integer.toString(pelaku))) {
                                                                                    clientBomb = clientBomb.replace(Integer.toString(X) + Integer.toString(Y) + Integer.toString(pelaku) + ",", "");
                                                                                }

                                                                                int radius = sBomb[noBomb].getRadius();
                                                                                if (player.getPosX() <= X + radius && player.getPosY() <= Y + radius && player.getPosX() >= X - radius && player.getPosY() >= Y - radius) {
                                                                                    player.kenaLedakan(sBomb[noBomb].getDamage());
                                                                                    System.out.println("Pelaku " + pelaku);

                                                                                    out.println("Score" + pelaku + "=1");
                                                                                    out.flush();
                                                                                }

                                                                                // Animasi ledakan
                                                                                for (int j = 0; j < 3; j++) {
                                                                                    try {
                                                                                        bn[j] = new Animasi(X, Y);
                                                                                        add(bn[j].doAnim(j), new Integer(2), 0);
                                                                                        Thread.sleep(200);
                                                                                        bn[j].setVisibility(false);
                                                                                    } catch (InterruptedException ex) {
                                                                                        Logger.getLogger(BomboMain.class.getName()).log(Level.SEVERE, null, ex);
                                                                                    }

                                                                                }
                                                                                posBomb = posBomb.replace(X + "-" + Y + "-" + pelaku + ",", "");
                                                                                sumberTimer.cancel();
                                                                            }
                                                                        };

                                                                        serverBomb[scount].schedule(sBomb[scount], 500, 1000);
                                                                        add(sBomb[scount].tampilkan(), new Integer(2), 0);
                                                                        System.out.println(scount);
                                                                        scount++;
                                                                        //} catch (IllegalStateException e) {
                                                                        //}
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    
                                                        String totalSkor = "Game result :\n";
                                                        for (int i = 0; i < 4; i++) {
                                                            totalSkor += "Player " + (i + 1) +" : "+ allPlayer[i].getScore() + "\n";
                                                        }
                                                        out.close();
                                                        in.close();
                                                        client.close();
                                                        server.close();
                                                        JOptionPane.showMessageDialog(null, totalSkor);
                                                    } catch (SocketException se) {
                                                    } catch (IOException ex) {
                                                    }

                                                }
                                            });
                                            handleInput.start();

                                            Timer isiBomb = new Timer();
                                            TimerTask tTask = new TimerTask() {
                                                @Override
                                                public void run() {
                                                    if (player.getBombBawa() < 5) {
                                                        player.increaseBomb();
                                                    }
                                                }
                                            };
                                            isiBomb.schedule(tTask, 0, 10000);

                                            PrintWriter out = new PrintWriter(client.getOutputStream());
                                            out.println("Player=" + ++currentPemain);

                                            out.print("Map=");
                                            out.flush();
                                            for (int i = 0; i < 16; i++) {
                                                for (int j = 0; j < 16; j++) {
                                                    out.print(papan[i][j]);
                                                    out.flush();
                                                }
                                            }
                                            out.println();
                                            out.flush();
                                            TimerTask waktu = new TimerTask() {
                                                @Override
                                                public void run() {
                                                    if (detik > 0) {
                                                        detik--;
                                                    }
                                                    int menit = detik / 60;
                                                    int dtk = detik % 60;
                                                    String tD = (dtk > 9 ? Integer.toString(dtk) : "0" + Integer.toString(dtk));
                                                    waktuPermainan = menit + ":" + tD;
                                                    tampilWaktu.setText(waktuPermainan);
                                                }
                                            };
                                            timerPermainan.schedule(waktu, 0, 1000);


                                            while (online) {
                                                tampilNyawa.setText("Life : " + player.getHealth());
                                                tampilBomb.setText("Bomb : " + player.getBombBawa());
                                                tampilScore.setText("Score : " + player.getScore());
                                                out.println("Time=" + waktuPermainan);
                                                // lemparkan data semua user ke masing - masing client
                                                out.println("Player1=" + player.getMatrixX() + "," + player.getMatrixY() + "," + player.getHealth());
                                                if (allPlayer[1].getComponent().isValid()) {
                                                    out.println("Player2=" + allPlayer[1].getMatrixX() + "," + allPlayer[1].getMatrixY() + "," + allPlayer[1].getHealth());
                                                }
                                                if (allPlayer[2].getComponent().isValid()) {
                                                    out.println("Player3=" + allPlayer[2].getMatrixX() + "," + allPlayer[2].getMatrixY() + "," + allPlayer[2].getHealth());
                                                }
                                                if (allPlayer[3].getComponent().isValid()) {
                                                    out.println("Player4=" + allPlayer[3].getMatrixX() + "," + allPlayer[3].getMatrixY() + "," + allPlayer[3].getHealth());
                                                }
                                                out.println("Bomb=" + posBomb);
                                                for (int i = 0; i < 4; i++) {
                                                    if (peledak[i] != 0) {
                                                        out.println("Score" + (i + 1) + "=" + peledak[i]);
                                                    }
                                                }

                                                out.flush();
                                                Thread.sleep(100);
                                            }

                                        } catch (IOException ex) {
                                            Logger.getLogger(BomboMain.class.getName()).log(Level.SEVERE, null, ex);
                                        } catch (InterruptedException ex) {
                                            Logger.getLogger(BomboMain.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                    }
                                });
                                sambungan.start();
                            } catch (IOException ex) {
                                Logger.getLogger(BomboMain.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }

                    }
                });
                serverisasi.start();
            } else {

                Thread clienisasi = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            Socket client = new Socket(ipServer, 6666);
                            final PrintWriter out = new PrintWriter(client.getOutputStream());
                            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                            String input = in.readLine();


                            // Cari nomor giliran player bermain
                            if (input.contains("Player=")) {
                                urutan = Integer.parseInt(input.substring(7));
                                window.setTitle("BomboMan | Player " + urutan);
                                setUp(urutan);
                                player.setNamaPemain(namaPemain);
                            }
                            out.println(namaPemain);
                            out.flush();
                            while (online) {
                                tampilNyawa.setText("Life : " + player.getHealth());
                                tampilBomb.setText("Bomb : " + player.getBombBawa());
                                tampilScore.setText("Score : " + player.getScore());

                                try {
                                    out.println("Player" + urutan + "=" + player.getMatrixX() + "," + player.getMatrixY() + "," + player.getHealth());
                                    out.println("B=" + posBomb);
                                    out.flush();

                                    input = in.readLine();

                                    if (input.contains("Score" + urutan)) {
                                        int skor = Integer.parseInt(input.substring(7));

                                        if (skor != 0) {
                                            System.out.println("Dapat Skor");
                                            player.setScore(player.getScore() + 500);
                                            tampilScore.setText("Score : " + player.getScore());
                                            out.println("RESET" + urutan);
                                        }
                                    } else if (input.contains("Bomb=")) {
                                        if (input.length() == 5) {
                                            counter = 0;
                                            al.clear();
                                        } else if (!input.equalsIgnoreCase(sama)) {
                                            sama = input;

                                            String potong[] = input.substring(5).split(",");

                                            for (int i = 0; i < potong.length; i++) {
                                                String xy[] = potong[i].split("-");

                                                if (!xy[0].isEmpty() && !xy[1].isEmpty() && !al.contains(xy[0] + xy[1])) {
                                                    al.add(xy[0] + xy[1]);
                                                    tBomb[counter] = new Timer();
                                                    final int pelaku = Integer.parseInt(xy[2]);
                                                    cBomb[counter] = new Bomb(Integer.parseInt(xy[2]), counter, Integer.parseInt(xy[0]), Integer.parseInt(xy[1]), tBomb[counter]) {
                                                        @Override
                                                        public void meledak(int noBomb, int X, int Y, Timer sumberTimer) {
                                                            cBomb[noBomb].setVisibility(false);
                                                            Animasi an[] = new Animasi[4];
                                                            int radius = cBomb[noBomb].getRadius();

                                                            if (player.getPosX() <= X + radius && player.getPosY() <= Y + radius && player.getPosX() >= X - radius && player.getPosY() >= Y - radius) {
                                                                player.kenaLedakan(cBomb[noBomb].getDamage());
                                                                System.out.println("Pelaku " + pelaku);
                                                                if (pelaku != urutan) {
                                                                    out.println("HIT" + pelaku);
                                                                    out.flush();
                                                                }
                                                            }

                                                            for (int i = 0; i < 3; i++) {
                                                                try {
                                                                    an[i] = new Animasi(X, Y);
                                                                    add(an[i].doAnim(i), new Integer(3), 0);
                                                                    Thread.sleep(200);
                                                                    an[i].setVisibility(false);
                                                                } catch (InterruptedException ex) {
                                                                    Logger.getLogger(BomboMain.class.getName()).log(Level.SEVERE, null, ex);
                                                                }
                                                            }
                                                            sumberTimer.cancel();
                                                            al.remove(X + "" + Y);
                                                            if (counter == noBomb) {
                                                                counter = 0;
                                                            }
                                                        }
                                                    };
                                                    tBomb[counter].schedule(cBomb[counter], 500, 1000);
                                                    add(cBomb[counter].tampilkan(), new Integer(2), 0);

                                                    counter++;

                                                }
                                            }

                                        }
                                    } else if (input.contains("Time=")) {
                                        String waktu = input.substring(5);
                                        tampilWaktu.setText(waktu);

                                        if (waktu.contains("0:00")) {
                                            out.println("SKORKU" + urutan + "=" + player.getScore());
                                            out.flush();
                                            online = false;
                                        }
                                    } else if (input.contains("Player1=")) {
                                        if (!allPlayer[0].getComponent().isValid() && urutan != 1) {
                                            add(allPlayer[0].tampilkan(), new Integer(4), 0);
                                        }
                                        String potong[] = input.substring(8).split(",");
                                        allPlayer[0].setMatrixX(Integer.parseInt(potong[0]));
                                        allPlayer[0].setMatrixY(Integer.parseInt(potong[1]));
                                        allPlayer[0].setHealth(Integer.parseInt(potong[2]));
                                    } else if (input.contains("Player2=")) {
                                        if (!allPlayer[1].getComponent().isValid() && urutan != 2) {
                                            add(allPlayer[1].tampilkan(), new Integer(4), 0);
                                        }

                                        String potong[] = input.substring(8).split(",");
                                        allPlayer[1].setMatrixX(Integer.parseInt(potong[0]));
                                        allPlayer[1].setMatrixY(Integer.parseInt(potong[1]));
                                        allPlayer[1].setHealth(Integer.parseInt(potong[2]));
                                    } else if (input.contains("Player3=")) {
                                        if (!allPlayer[2].getComponent().isValid() && urutan != 3) {
                                            add(allPlayer[2].tampilkan(), new Integer(4), 0);
                                        }
                                        String potong[] = input.substring(8).split(",");
                                        allPlayer[2].setMatrixX(Integer.parseInt(potong[0]));
                                        allPlayer[2].setMatrixY(Integer.parseInt(potong[1]));
                                        allPlayer[2].setHealth(Integer.parseInt(potong[2]));
                                    } else if (input.contains("Player4=")) {
                                        if (!allPlayer[3].getComponent().isValid() && urutan != 4) {
                                            add(allPlayer[3].tampilkan(), new Integer(4), 0);
                                        }
                                        String potong[] = input.substring(8).split(",");
                                        allPlayer[3].setMatrixX(Integer.parseInt(potong[0]));
                                        allPlayer[3].setMatrixY(Integer.parseInt(potong[1]));
                                        allPlayer[3].setHealth(Integer.parseInt(potong[2]));
                                    }

                                } catch (SocketException ex) {
                                    JOptionPane.showMessageDialog(null, "Disconnected from server, game will be over.");
                                    break;
                                }
                            }
                            out.println("MATI" + urutan);

                            out.flush();
                        } catch (UnknownHostException ex) {
                            Logger.getLogger(BomboMain.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            Logger.getLogger(BomboMain.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });
                clienisasi.start();
            }

        } catch (IOException ex) {
            Logger.getLogger(BomboMain.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    class WindowEventHandler extends WindowAdapter {

        public void windowClosing(WindowEvent evt) {
            if (!isServer) {
                online = false;
            }
        }
    }

    private void setFrame() {
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setResizable(false);
        this.window = frame;
        frame.setTitle("BomboMan");
        this.setPreferredSize(new Dimension(540, 590));
        this.setBounds(0, 0, 540, 590);
        frame.addWindowListener(new WindowEventHandler());
        frame.setSize(this.getPreferredSize());
        frame.setLocationRelativeTo(null);
        frame.add(this);
        frame.setVisible(true);
    }

    private void setUp(int Player) {
        papan = new int[][]{
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 1},
            {1, 0, 1, 0, 0, 0, 1, 1, 1, 1, 0, 0, 1, 1, 0, 1},
            {1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 1},
            {1, 0, 1, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 1, 1, 1},
            {1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1},
            {1, 0, 1, 0, 1, 0, 1, 1, 1, 1, 0, 1, 1, 0, 0, 1},
            {1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 1},
            {1, 0, 0, 1, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1},
            {1, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}};


        JLabel denah[][] = new JLabel[50][50];
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                denah[x][y] = new JLabel();
                denah[x][y].setBounds((x * 32) + 10, (y * 32) + 10, 32, 32);
                switch (papan[x][y]) {
                    case 0: {
                        denah[x][y].setIcon(new ImageIcon(this.getClass().getResource("gambar/rumput.png")));
                    }
                    break;
                    case 1: {
                        denah[x][y].setIcon(new ImageIcon(this.getClass().getResource("gambar/tembok.png")));
                    }
                    break;
                }

                add(denah[x][y], new Integer(2), 0);

            }
        }

        player = new Pemain(Player, bawaBomb, papan) {
            @Override
            public void tampil_bomb(Component b, String[] listBomb) {
                add(b, new Integer(2), 0);
                posBomb = "";
                for (int i = 0; i < listBomb.length - 1; i++) {
                    if ((listBomb[i] != null) && (listBomb[i].length() > 0)) {
                        posBomb += listBomb[i] + ",";
                    }
                }

            }
        };
        add(player.tampilkan(), new Integer(4), 0);
        player.getComponent().requestFocus();

        Font myFont = new Font("Verdana", Font.BOLD, 14);

        tampilNyawa.setBounds(10, 530, 120, 20);
        tampilNyawa.setText("Life : " + player.getHealth());
        tampilNyawa.setFont(myFont);
        add(tampilNyawa, new Integer(2), 0);

        tampilBomb.setBounds(450, 530, 120, 20);
        tampilBomb.setText("Bomb : " + player.getBombBawa());
        tampilBomb.setFont(myFont);
        add(tampilBomb, new Integer(2), 0);

        tampilScore.setBounds(230, 530, 120, 20);
        tampilScore.setText("Score : " + player.getScore());
        tampilScore.setFont(myFont);
        add(tampilScore, new Integer(2), 0);


        tampilWaktu.setBounds(240, 18, 120, 20);
        tampilWaktu.setText(waktuPermainan);
        tampilWaktu.setForeground(Color.white);
        tampilWaktu.setFont(new Font("Comic Sans MS", Font.BOLD, 16));
        add(tampilWaktu, new Integer(5), 0);
    }
}

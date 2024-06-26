import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

public class ServerCW {

    static ArrayList<MyFile> myFiles = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        int fileId=0;
        JFrame jFrame = new JFrame("Your Server");
        jFrame.setSize(400, 400);
        jFrame.setLayout(new BoxLayout(jFrame.getContentPane(), BoxLayout.Y_AXIS));;
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));

        JScrollPane jScrollPane = new JScrollPane(jPanel);
        jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JLabel jlTitle = new JLabel("File Receiver");
        jlTitle.setFont(new Font("Arial", Font.BOLD, 25));
        jlTitle.setBorder(new EmptyBorder(20, 0, 10, 0));

        jFrame.add(jlTitle);
        jFrame.add(jScrollPane);
        jFrame.setVisible(true);

        try (ServerSocket serverSocket = new ServerSocket(1237)) {
            //public static void handleClient(Socket socket) {
            while(true){

            
            try {
                Socket socket=serverSocket.accept();
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                int fileNameLength = dataInputStream.readInt();
                if (fileNameLength > 0) {
                    byte[] fileNameBytes = new byte[fileNameLength];
                    dataInputStream.readFully(fileNameBytes, 0, fileNameBytes.length);
                    String fileName=new String(fileNameBytes);
                    int fileContentLength = dataInputStream.readInt();

                    if (fileContentLength > 0) {
                        byte[] fileContentBytes = new byte[fileContentLength];
                        dataInputStream.readFully(fileContentBytes, 0, fileContentLength);

                        JPanel jpfileRow=new JPanel();
                        jpfileRow.setLayout(new BoxLayout(jpfileRow,BoxLayout.Y_AXIS));


                        JLabel jlFileName=new JLabel(fileName);
                        jlFileName.setFont(new Font("Arial",Font.BOLD,25));
                        jlFileName.setBorder(new EmptyBorder(10, 0, 10, 0));

// Logic for showing file in the server panel
                        if(getFileExtension(fileName).equalsIgnoreCase("txt")){
                            jpfileRow.setName(String.valueOf(fileId));
                            jpfileRow.addMouseListener(getMyMouseListener());

                            jpfileRow.add(jlFileName);
                            jPanel.add(jpfileRow);
                            jFrame.validate();

                        }else{
                            jpfileRow.setName(String.valueOf(fileId));
                            jpfileRow.addMouseListener(getMyMouseListener());

                            jpfileRow.add(jlFileName);
                            jPanel.add(jpfileRow);
                            jFrame.validate();
                        }
                        myFiles.add(new MyFile(fileId, fileName, fileContentBytes, getFileExtension(fileName)));
                        fileId++;

                    }
                }
                
            } catch (IOException error) {
                error.printStackTrace();
            }
   }
        }

            //         // Handle the client connection in a separate thread
            //         // Thread clientThread = new Thread(() -> handleClient(socket));
            //         // clientThread.start();

            //     } catch (IOException error) {
            //         error.printStackTrace();
            //     }
            // }

}

public static MouseListener getMyMouseListener() {
    return new MouseListener() {
        @Override
        public void mouseClicked(MouseEvent e) {
            JPanel jPanel = (JPanel) e.getSource();
            int fileId = Integer.parseInt(jPanel.getName());
            for (MyFile myFile : myFiles) {
                if (myFile.getId() == fileId) {
                    JFrame jfPreview = createFrame(myFile.getName(), myFile.getData(), myFile.getFileExtension());
                    jfPreview.setVisible(true);
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
    };
}

public static JFrame createFrame(String fileName, byte[] fileData, String fileExtension) {
    JFrame jFrame = new JFrame("File Downloader");
    jFrame.setSize(400, 400);

    JPanel jPanel = new JPanel();
    jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));

    JLabel jltitle = new JLabel("File DOWNLOADER");
    jltitle.setAlignmentX(Component.CENTER_ALIGNMENT);
    jltitle.setFont(new Font("Arial", Font.BOLD, 20));
    jltitle.setBorder(new EmptyBorder(20, 0, 10, 0));

    JLabel jlPrompt = new JLabel("Are you sure you want to download " + fileName);
    jlPrompt.setFont(new Font("Arial", Font.BOLD, 20));
    jlPrompt.setBorder(new EmptyBorder(20, 0, 10, 0));
    jlPrompt.setAlignmentX(Component.CENTER_ALIGNMENT);

    JButton jbYes = new JButton("YES");
    jbYes.setPreferredSize(new Dimension(150, 75));
    jbYes.setFont(new Font("Arial", Font.BOLD, 25));

    JButton jbNo = new JButton("NO");
    jbNo.setPreferredSize(new Dimension(150, 75));
    jbNo.setFont(new Font("Arial", Font.BOLD, 25));

    JLabel jlFileContent = new JLabel();
    jlFileContent.setAlignmentX(Component.CENTER_ALIGNMENT);

    JPanel jpButton = new JPanel();
    jpButton.setBorder(new EmptyBorder(20, 0, 10, 0));
    jpButton.add(jbYes);
    jpButton.add(jbNo);

    // // Logic
    // if (fileExtension.equalsIgnoreCase("txt")) {
    //     jlFileContent.setText("<html>" + new String(fileData) + "</html>");
    // } else {
    //     jlFileContent.setIcon(new ImageIcon(fileData));
    // }

    // Logic for yes button
    jbYes.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            //File fileToDownload = new File(fileName);
            if(fileExtension.equalsIgnoreCase("txt")){
            try {
                File DownloadedFile =new File(fileName);

                FileOutputStream fileOutputStream = new FileOutputStream(DownloadedFile);
                fileOutputStream.write(fileData);
                fileOutputStream.close();

                jFrame.dispose();
            } catch (IOException error) {
                error.printStackTrace();
            }
        }else{

        }
        }
    });

    jbNo.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            jFrame.dispose();
        }
    });

    jPanel.add(jltitle);
    jPanel.add(jlPrompt);
    jPanel.add(jlFileContent);
    jPanel.add(jpButton);

    jFrame.add(jPanel);
return jFrame;
}

public static String getFileExtension(String fileName) {
    int i = fileName.lastIndexOf('.');
    if (i > 0) {
        return fileName.substring(i + 1);
    } else {
        return "No extension found";
    }
}
}



package messangerclient;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.StringTokenizer;
import static messangerclient.ClientConstant.*;

public class LoginFrame extends javax.swing.JFrame {
    /** Creates new form LoginFrame */
    LogInPanel loginP;
    ClientListPanel buddyList;
    ClientManager clientManager;
    ClientStatusListener clientStatus;
    ClientListListener clientListListener;
    ClientWindowListener clientWindowListener;
    String userName;
    int messagingFrameNo = 0;
    MessagingFrame [] messagingFrames;

    public LoginFrame(ClientManager getClientManager) {
        clientStatus = new MyClientStatus();
        clientListListener = new MyClientListListener();
        clientWindowListener = new MyClientWindowListener();
        messagingFrames = new MessagingFrame[10000];
        initComponents();
        clientManager = getClientManager;
        myPanel.setLayout(new BorderLayout());
        addLogInPanel();
        logout_button.setVisible(false);
    }

    void addLogInPanel() {
        loginP = new LogInPanel();
        myPanel.add(loginP, BorderLayout.CENTER);
        setVisible(true);

        loginP.but_signin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                but_signinActionPerformed();
            }
        });
    }

    void addBuddyList() {
            buddyList = new ClientListPanel();
            myPanel.add(buddyList, BorderLayout.CENTER);
            setVisible(true);

            buddyList.list_online_clients.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent me) {
                    if(me.getClickCount()==2) {  // Double click
                        String to = (String)buddyList.list_online_clients.getSelectedValue();
                        boolean isWindowOpen = false;
                        for(int i=0; i<messagingFrameNo; ++i) {
                            if(messagingFrames[i].to.equalsIgnoreCase(to)) {
                                isWindowOpen = true;
                                break;
                            }
                        }
                        if(!isWindowOpen) {
                            messagingFrames[messagingFrameNo] = new MessagingFrame(to, userName, clientManager);
                            messagingFrames[messagingFrameNo].setVisible(true);
                            messagingFrameNo++;
                        }
                    }
                }
            });
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        myPanel = new javax.swing.JPanel();
        lb_status = new javax.swing.JLabel();
        logout_button = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        my_sign_in = new javax.swing.JMenuItem();
        my_sign_out = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Login Window");

        javax.swing.GroupLayout myPanelLayout = new javax.swing.GroupLayout(myPanel);
        myPanel.setLayout(myPanelLayout);
        myPanelLayout.setHorizontalGroup(
            myPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        myPanelLayout.setVerticalGroup(
            myPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 314, Short.MAX_VALUE)
        );

        lb_status.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lb_status.setForeground(new java.awt.Color(255, 0, 0));
        lb_status.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lb_status.setText("You are not connected to server");

        logout_button.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        logout_button.setForeground(new java.awt.Color(51, 0, 204));
        logout_button.setText("Log Out");
        logout_button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        logout_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logout_buttonActionPerformed(evt);
            }
        });

        jMenu1.setText("User");

        my_sign_in.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
        my_sign_in.setText("Sign In");
        jMenu1.add(my_sign_in);

        my_sign_out.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.CTRL_MASK));
        my_sign_out.setText("Sign Out");
        my_sign_out.setEnabled(false);
        my_sign_out.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                my_sign_outActionPerformed(evt);
            }
        });
        jMenu1.add(my_sign_out);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lb_status, javax.swing.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE)
            .addComponent(myPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(73, 73, 73)
                .addComponent(logout_button, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(myPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lb_status)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(logout_button)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void my_sign_outActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_my_sign_outActionPerformed
        myPanel.remove(buddyList);
        clientManager.sendMessage(DISCONNECT_STRING + " " + userName);
        clientManager.flagOutput=true;
        addLogInPanel();
        my_sign_out.setEnabled(false);
        my_sign_in.setEnabled(true);
        clientManager.disconnect(clientStatus);
        System.exit(0);
    }//GEN-LAST:event_my_sign_outActionPerformed

    private void logout_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logout_buttonActionPerformed
        myPanel.remove(buddyList);
        clientManager.sendMessage(DISCONNECT_STRING + " " + userName);
        clientManager.flagOutput=true;
        addLogInPanel();
        my_sign_out.setEnabled(false);
        my_sign_in.setEnabled(true);
        clientManager.disconnect(clientStatus);
        System.exit(0);
    }//GEN-LAST:event_logout_buttonActionPerformed

    private void but_signinActionPerformed() {
        if(!loginP.tf_user_name.getText().isEmpty()) {
            myPanel.remove(loginP);
            clientManager.connect(clientStatus);
            addBuddyList();
            userName = loginP.tf_user_name.getText();
            setTitle("Messenger (" + userName + ")");
            clientManager.sendMessage("login " + userName);
            clientManager.receiveMessage(clientListListener, clientWindowListener);
            my_sign_in.setEnabled(false);
            my_sign_out.setEnabled(true);
            logout_button.setVisible(true);
        }
        else
            javax.swing.JOptionPane.showMessageDialog(this, "Please enter your Name ");
    }

    class MyClientStatus implements ClientStatusListener {
        @Override
        public void loginStatus(String status) {
            lb_status.setText(status);
        }
    }

    class MyClientListListener implements ClientListListener {
        @Override
        public void addToList(String usersName) {
            if(!usersName.equalsIgnoreCase(userName))
                buddyList.list_model.addElement(usersName);
        }
        @Override
        public void removeFromList(String userName) {
            buddyList.list_model.removeElement(userName);
        }
    }

    class MyClientWindowListener implements ClientWindowListener {
        @Override
        public void openWindow(String message) {
            boolean isWindowOpen = false;
            int openWindowNo = 0;

            StringTokenizer tokens = new StringTokenizer(message);
            String to = tokens.nextToken();
            String from = tokens.nextToken();
            for(int i=0; i<messagingFrameNo; i++) {
                if(messagingFrames[i].to.equalsIgnoreCase(from)) {
                    isWindowOpen = true;
                    openWindowNo = i;
                    break;
                }
            }

            if(isWindowOpen)
                messagingFrames[openWindowNo].view_message.append(message.replaceFirst(to, "") + "\n");
            else {
                messagingFrames[messagingFrameNo] = new MessagingFrame(from, userName, clientManager);
                messagingFrames[messagingFrameNo].setVisible(true);
                messagingFrames[messagingFrameNo].view_message.append(message.replaceFirst(to, "") + "\n");
                messagingFrameNo++;
            }
        }
        
        @Override
        public void closeWindow(String getMessage) {
            myPanel.remove(buddyList);
            addLogInPanel();
            my_sign_out.setEnabled(false);
            my_sign_in.setEnabled(true);
            lb_status.setText(getMessage);
        }
        
        @Override
        public void fileStatus(String filesStatus) {
            lb_status.setText(filesStatus);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    public javax.swing.JLabel lb_status;
    private javax.swing.JButton logout_button;
    private javax.swing.JPanel myPanel;
    private javax.swing.JMenuItem my_sign_in;
    private javax.swing.JMenuItem my_sign_out;
    // End of variables declaration//GEN-END:variables
}
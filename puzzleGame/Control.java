package puzzleGame;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.List;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import static java.util.Collections.list;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import sun.security.util.ArrayUtil;

public class Control {

    private PuzzleGUI pz;
    private int size = 0;// size của game
    private JButton[][] matrix;// mảng 2 chiều  để chứa các button
    private int countMove = 0;// đếm số lần đi
    private Timer timer; 
    private int countTime = 0;//đếm time
    private boolean isStart = false;// để có thể tạo mới thời gian
    private boolean isNewGame = true;// để check tạo new game

    public Control(PuzzleGUI pz) {
        this.pz = pz;
        setSize();
        addButton();
    }

    public void newGame() {
        isNewGame = true;
        countMove = 0;
        countTime = 0;
        pz.lblCountMove.setText("0");
        pz.lblCountTime.setText("0");
        addButton();
        countTime();
        isStart = true;
    }

    public void setSize() {
        Toolkit t = Toolkit.getDefaultToolkit();
        Dimension d = t.getScreenSize();
        int lastIndex = (d.height - 200) / 100;
        for (int i = 3; i <= lastIndex; i++) {
            pz.cbxSize.addItem(i + "x" + i);
        }
    }

    //add button vao panel
    public void addButton() {
        //int size = pz.cbxSize.getSelectedIndex()+3; lấy size từ combobox rồi +3;
        String txt = pz.cbxSize.getSelectedItem().toString();//3x3; 4x4
        size = Integer.parseInt(txt.split("x")[0]);
        pz.getPnLayout().removeAll();// xóa tất cả các cái cũ 
        pz.pnLayout.setLayout(new GridLayout(size, size, 10, 10));// set layout cho Panel và có 
                                                                   //khoảng cách giữa các button =10
        pz.pnLayout.setPreferredSize(new Dimension(60 * size, 60 * size)); //set size của panel có chiều dài và rộng
                                                                           // bằng size*60
        matrix = new JButton[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                JButton btn = new JButton(i * size + j + 1 + "");// set text cho button
                matrix[i][j] = btn;
                pz.pnLayout.add(btn);
                btn.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (!isNewGame) {
                            JOptionPane.showMessageDialog(null, "Press new game button");
                            return;
                        }
                        if (checkMove(btn)) {// nếu điều kiện để move = true thì đc phép di chuyển các button
                            moveButton(btn);
                            if (checkWin()) {// nếu win thì reset lại mọi thứ
                                timer.stop();
                                isStart = false;
                                isNewGame = false;
                                JOptionPane.showMessageDialog(pz, "You Win");
                            }
                        }
                    }
                });
            }
        }
        matrix[size - 1][size - 1].setText("");//set button cuối cùng có text = rỗng
        mixButton();// đảo các button
      
        pz.pack();
    }

    //tìm vị trí của button rỗng
    public Point getPosEmpty() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (matrix[i][j].getText().equals("")) {
                    return new Point(i, j);
                }
            }
        }
        return null;
    }

    // đảo các button
    public void mixButton() {
        for (int k = 0; k < 1000; k++) {
            Point p = getPosEmpty();
            int i = p.x;
            int j = p.y;
            Random r = new Random();
            int choice = r.nextInt(4);// random các số 0,1,2,3
            String txt = "";
            switch (choice) {
                case 0: //đối với các button nằm trên cùng
                    if (i > 0) {
                        txt = matrix[i - 1][j].getText();
                        matrix[i][j].setText(txt);
                        matrix[i - 1][j].setText("");
                    }
                    break;
                case 1://đối với các button nằm dưới cùng
                    if (i < size - 1) {
                        txt = matrix[i + 1][j].getText();
                        matrix[i][j].setText(txt);
                        matrix[i + 1][j].setText("");
                    }
                    break;
                case 2://đối với các button ở trái lề
                    if (j > 0) {
                        txt = matrix[i][j - 1].getText();
                        matrix[i][j].setText(txt);
                        matrix[i][j - 1].setText("");
                    }
                    break;
                case 3://đối với các button ở phải lề
                    if (j < size - 1) {
                        txt = matrix[i][j + 1].getText();
                        matrix[i][j].setText(txt);
                        matrix[i][j + 1].setText("");
                    }
                    break;
            }
        }
    }

    //check xem button click vào có được đổi với button rỗng hay không
    public Point getPosClick(JButton btn) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (matrix[i][j].getText().equals(btn.getText())) {
                    return new Point(i, j);
                }
            }
        }
        return null;
    }
    //check điều kiện để di chuyển
    public boolean checkMove(JButton btn) {
        if (btn.getText().equals("")) {
            return false;
        }
        Point cb = getPosClick(btn);
        Point eb = getPosEmpty();
        if (cb.x == eb.x && Math.abs(cb.y - eb.y) == 1) {// true nếu x ở button click = x ở button rỗng và y ở button click
            return true;                                 // trừ đi y ở button rỗng có giá trị tuyệt đối =1
        }
        if (cb.y == eb.y && Math.abs(cb.x - eb.x) == 1) {// true nếu y ở button click = y ở button rỗng và x ở button click
            return true;                                // trừ đi x ở button rỗng có giá trị tuyệt đối =1
        }
        return false;
    }
//di chuyển button
    public void moveButton(JButton btn) {
        Point eb = getPosEmpty();
        String txt = btn.getText();
        matrix[eb.x][eb.y].setText(txt); //set text ở button rỗng thành text ở button click vào
        btn.setText(""); //button click vào set lại bằng rỗng
        countMove++;
        pz.lblCountMove.setText(countMove + "");
    }
    //check điều kiện để win
    public boolean checkWin() {
        if (!matrix[size - 1][size - 1].getText().equals("")) { // false nếu button cuối khác rỗng
            return false;
        }
        int number = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                number++;
                String txt = matrix[i][j].getText();
                if (txt.equals("")) {
                    txt = size * size + "";//nếu text ở button cuối = rỗng thì txt bằng size*size
                }
                int value = Integer.parseInt(txt);
                if (number != value) {//nếu text lần lượt ở các button khác với number thì = false
                    return false;
                }
            }
        }
        return true; 
    }
    // thời gian chơi
    public void countTime() {
        if (isStart) { // nếu true thì thời gian dc dừng lại
            timer.stop();
        }
        timer = new Timer(1000, new ActionListener() {// thời gian nghỉ là 1s
            @Override
            public void actionPerformed(ActionEvent e) {
                countTime++;
                pz.lblCountTime.setText(countTime + "");
            }
        });
        timer.start();
    }

}

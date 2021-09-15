
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class ChessBoard {

	public static final int BLACK = 0, WHITE = 1;
	public static final int QUEEN = 0, KING = 1, ROOK = 2, KNIGHT = 3, BISHOP = 4, PAWN = 5;
	public static final int[] STARTING_ROW = { ROOK, KNIGHT, BISHOP, KING, QUEEN, BISHOP, KNIGHT, ROOK };

	private static final String COLS = "ABCDEFGH";

	private final JPanel chessPanel = new JPanel(new BorderLayout(3, 3));
	private final JLabel message = new JLabel("Hoàng Thị Thanh Tún.");

	private JButton[][] chessBoardSquares = new JButton[8][8];
	private Image[][] chessPieceImages = new Image[2][6];
	private JPanel chessBoard;
	private JButton nextButton;
	private Action autoAction;
	private Action nextAction;
	private Action startAction;

	private int lastX, lastY;
	private int moveNumber = 1;
	private int[][] results;
	private Timer timer;
	private KnightsTour kt = new KnightsTour();

	public static void main(String[] args) {
		Runnable r = new Runnable() {

			@Override
			public void run() {
				ChessBoard cb = new ChessBoard();

				JFrame f = new JFrame("Nhóm 4 - huce");
				f.getContentPane().add(cb.getChessPanel());
				f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				f.setLocationByPlatform(true);

				f.pack();

				f.setMinimumSize(f.getSize());
				f.setVisible(true);
			}
		};
		SwingUtilities.invokeLater(r);
	}

	public ChessBoard() {
		initialize();
	}

	public final JComponent getChessPanel() {
		return chessPanel;
	}

	private final void createImages() {
		try {
			InputStream in = ChessBoard.class.getResourceAsStream("/chess-pieces.png");
			BufferedImage bi = ImageIO.read(in);
			for (int i = 0; i < 2; i++) {
				for (int j = 0; j < 6; j++) {
					chessPieceImages[i][j] = bi.getSubimage(j * 64, i * 64, 48, 48);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	private void initialize() {
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		createImages();

		JToolBar tools = new JToolBar();
		tools.setFloatable(false);
		chessPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		chessPanel.add(tools, BorderLayout.PAGE_START);

		startAction = new AbstractAction("Start") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				startKnightsTour();
				results = kt.run(true);
				nextButton.setEnabled(true);
				autoAction.setEnabled(true);
				this.setEnabled(false);
				if (timer.isRunning()) {
					timer.stop();
				}
			}
		};

		nextAction = new NextAction("Next");
		nextButton = new JButton(nextAction);
		nextButton.setEnabled(false);

		timer = new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				nextButton.doClick();
			}
		});

		autoAction = new AbstractAction("Auto") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				this.setEnabled(false);
				timer.start();
			}
		};
		autoAction.setEnabled(false);

		Action resetAction = new AbstractAction("Reset") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				JButton b;
				for (int i = 0; i < chessBoardSquares.length; i++) {
					for (int j = 0; j < chessBoardSquares[i].length; j++) {
						b = chessBoardSquares[i][j];
						b.setIcon(null);
						b.setText(null);
						if (((j % 2 == 1) && (i % 2 == 1)) || ((j % 2 == 0) && (i % 2 == 0))) {
							b.setBackground(Color.WHITE);
						} else {
							b.setBackground(Color.BLACK);
						}
					}
				}
				nextButton.setEnabled(false);
				startAction.setEnabled(true);
				moveNumber = 1;
				message.setText("Ready to go.");
			}
		};

		Action exitAction = new AbstractAction("Exit") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO: Do we need any clean-up?
				System.exit(0);
			}
		};

		tools.add(startAction);
		tools.add(nextButton);
		tools.add(autoAction);
		tools.addSeparator();
		tools.add(resetAction);
		tools.addSeparator();
		tools.add(exitAction);
		tools.addSeparator();
		tools.add(message);

		chessPanel.add(new JLabel("?"), BorderLayout.LINE_START);

		chessBoard = new JPanel(new GridLayout(0, 9));
		chessBoard.setBorder(new LineBorder(Color.BLACK));
		chessPanel.add(chessBoard);

		fillBoard();

		chessBoard.add(new JLabel(""));
		for (int i = 0; i < 8; i++) {
			chessBoard.add(new JLabel(COLS.substring(i, i + 1), SwingConstants.CENTER));
		}

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				switch (j) {
					case 0:
						chessBoard.add(new JLabel("" + (8 - i), SwingConstants.CENTER));
					default:
						chessBoard.add(chessBoardSquares[j][i]);
				}
			}
		}
	}

	private void fillBoard() {
		Insets buttonMargin = new Insets(0, 0, 0, 0);
		for (int i = 0; i < chessBoardSquares.length; i++) {
			for (int j = 0; j < chessBoardSquares[i].length; j++) {
				JButton b = new JButton();
				b.setMargin(buttonMargin);

				ImageIcon icon = new ImageIcon(new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB));
				b.setIcon(icon);
				if (((j % 2 == 1) && (i % 2 == 1)) || ((j % 2 == 0) && (i % 2 == 0))) {
					b.setBackground(Color.WHITE);
				} else {
					b.setBackground(Color.BLACK);
				}
				chessBoardSquares[j][i] = b;
			}
		}
	}

	private final void placeAllStartingPieces() {
		message.setText("Make your move!");
		for (int i = 0; i < STARTING_ROW.length; i++) {
			chessBoardSquares[i][0].setIcon(new ImageIcon(chessPieceImages[BLACK][STARTING_ROW[i]]));
		}
		for (int i = 0; i < STARTING_ROW.length; i++) {
			chessBoardSquares[i][1].setIcon(new ImageIcon(chessPieceImages[BLACK][PAWN]));
		}
		for (int i = 0; i < STARTING_ROW.length; i++) {
			chessBoardSquares[i][6].setIcon(new ImageIcon(chessPieceImages[WHITE][PAWN]));
		}
		for (int i = 0; i < STARTING_ROW.length; i++) {
			chessBoardSquares[i][7].setIcon(new ImageIcon(chessPieceImages[WHITE][STARTING_ROW[i]]));
		}
	}

	private final void startKnightsTour() {
		message.setText("Chạy!");
		chessBoardSquares[0][0].setIcon(new ImageIcon(chessPieceImages[BLACK][STARTING_ROW[1]]));
	}

	class NextAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		NextAction(String text) {
			super(text);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (moveNumber > 1) {
				chessBoardSquares[lastX][lastY].setIcon(null);
				chessBoardSquares[lastX][lastY].setBackground(Color.PINK);
				chessBoardSquares[lastX][lastY].setText(String.valueOf(moveNumber));

			}

			if (moveNumber == 64) {
				this.setEnabled(false);
				nextButton.setEnabled(false);
				timer.stop();
				message.setText("Hoàn thành.");
			}

			for (int i = 0; i < KnightsTour.N; i++) {
				for (int j = 0; j < KnightsTour.N; j++) {
					if (results[i][j] == moveNumber) {
						message.setText(String.format("Chạy tới (%d, %d). Lần chạy #%d.", i, j, moveNumber));
						chessBoardSquares[i][j].setIcon(new ImageIcon(chessPieceImages[BLACK][STARTING_ROW[1]]));
						lastX = i;
						lastY = j;
						break;
					}
				}
			}
			moveNumber++;
		}
	}
}

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.rsbot.Configuration;
import org.rsbot.event.events.MessageEvent;
import org.rsbot.event.listeners.MessageListener;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.gui.AccountManager;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.util.Filter;
import org.rsbot.script.wrappers.RSArea;
import org.rsbot.script.wrappers.RSComponent;
import org.rsbot.script.wrappers.RSGroundItem;
import org.rsbot.script.wrappers.RSItem;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSPlayer;
import org.rsbot.script.wrappers.RSTile;
import org.rsbot.script.wrappers.RSTilePath;

@ScriptManifest(authors = "Mr. Byte", name = "Byte's Wine Grabber", version = 1.20, description = "Snags the Wine of Zamorak", website = "http://LetTheSmokeOut.com")
public class BytesWineGrabber extends Script implements PaintListener,
		MessageListener, MouseListener {

	public class GUI extends JFrame {
		private JPanel mainPanel;
		private JLabel titleText;
		private JLabel titleSubText;
		private JButton runButton;
		private JButton cancelButton;
		private JCheckBox guiWorldHop;
		private JCheckBox guiCheckForUpdates;
		private JCheckBox guiRestForTheWicked;
		private JSlider guiLawsToWaste;
		private JSlider guiPlayerCountToHop;
		private JSlider HPPercentToEat;

		public GUI() {
			initComponents();
		}

		private void cancelButtonPressed(final ActionEvent e) {
			guiExit = true;
			gui.dispose();
			return;
		}

		private void guiLawsToWasteStateChanged(final ChangeEvent e) {
			guiLawsToWaste.setBorder(new TitledBorder("Hop After "
					+ guiLawsToWaste.getValue() + " Misses"));
		}

		private void guiPlayerCountToHopStateChanged(final ChangeEvent e) {
			guiPlayerCountToHop.setBorder(new TitledBorder("Hop if "
					+ guiPlayerCountToHop.getValue() + " Others in Temple"));
		}

		private void HPPercentToEatStateChanged(final ChangeEvent e) {
			HPPercentToEat.setBorder(new TitledBorder("Eat at "
					+ HPPercentToEat.getValue() + "% HP"));
			return;
		}

		private void initComponents() {
			mainPanel = new JPanel();
			titleText = new JLabel();
			titleSubText = new JLabel();
			runButton = new JButton();
			cancelButton = new JButton();
			guiWorldHop = new JCheckBox();
			guiCheckForUpdates = new JCheckBox();
			guiRestForTheWicked = new JCheckBox();
			guiLawsToWaste = new JSlider();
			guiPlayerCountToHop = new JSlider();
			HPPercentToEat = new JSlider();

			// ======== this ========
			setTitle("Byte's Wine Grabber v" + version);
			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(final WindowEvent e) {
					thisWindowClosing(e);
				}
			});
			final Container contentPane = getContentPane();
			contentPane.setLayout(null);

			// ======== mainPanel ========
			{
				mainPanel.setLayout(null);

				// ---- titleText ----
				titleText.setText("Byte's Wine Grabber v" + version);
				titleText.setHorizontalAlignment(SwingConstants.CENTER);
				titleText.setFont(new Font("Courier New", Font.PLAIN, 24));
				mainPanel.add(titleText);
				titleText.setBounds(5, 5, 480, 35);

				// ---- titleSubText ----
				titleSubText.setText("Snags the Wine of Zamorak");
				titleSubText.setHorizontalAlignment(SwingConstants.CENTER);
				titleSubText.setFont(new Font("Courier", Font.PLAIN, 14));
				mainPanel.add(titleSubText);
				titleSubText.setBounds(5, 45, 480, 20);

				// ---- runButton ----
				runButton.setText("Let's Snag Some Wine!");
				runButton.setFont(new Font("Arial", Font.PLAIN, 12));
				runButton.addActionListener(new ActionListener() {
					public void actionPerformed(final ActionEvent e) {
						runButtonPressed(e);
					}
				});
				mainPanel.add(runButton);
				runButton.setBounds(5, 336, 235, 30);

				// ---- cancelButton ----
				cancelButton.setText("Err, Sorry...My Wand Needs Polishing...");
				cancelButton.setFont(new Font("Arial", Font.PLAIN, 12));
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(final ActionEvent e) {
						cancelButtonPressed(e);
					}
				});
				mainPanel.add(cancelButton);
				cancelButton.setBounds(245, 336, 235, 30);

				// ---- guiWorldHop ----
				guiWorldHop.setText("World Hop?");
				guiWorldHop.setFont(new Font("Courier New", Font.PLAIN, 14));
				guiWorldHop.setHorizontalAlignment(SwingConstants.LEFT);
				guiWorldHop.addItemListener(new ItemListener() {
					public void itemStateChanged(final ItemEvent e) {
						worldHopItemStateChanged(e);
					}
				});
				mainPanel.add(guiWorldHop);
				guiWorldHop.setBounds(15, 75, 210, 30);

				// ---- guiCheckForUpdates ----
				guiCheckForUpdates.setText("Check online for Updates?");
				guiCheckForUpdates.setFont(new Font("Courier New", Font.PLAIN, 14));
				mainPanel.add(guiCheckForUpdates);
				guiCheckForUpdates.setBounds(121, 297, 255, 30);

				// ---- guiRestForTheWicked ----
				guiRestForTheWicked.setText("Rest?");
				guiRestForTheWicked.setFont(new Font("Courier New", Font.PLAIN, 14));
				guiRestForTheWicked.setHorizontalAlignment(SwingConstants.RIGHT);
				mainPanel.add(guiRestForTheWicked);
				guiRestForTheWicked.setBounds(260, 75, 210, 30);

				// ---- guiLawsToWaste ----
				guiLawsToWaste.setMaximum(8);
				guiLawsToWaste.setMinorTickSpacing(1);
				guiLawsToWaste.setPaintLabels(true);
				guiLawsToWaste.setPaintTicks(true);
				guiLawsToWaste.setSnapToTicks(true);
				guiLawsToWaste.setValue(5);
				guiLawsToWaste.setMajorTickSpacing(1);
				guiLawsToWaste.setBorder(new TitledBorder(null, "Hop After "
						+ guiLawsToWaste.getValue() + " Misses", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Courier New", Font.PLAIN, 12)));
				guiLawsToWaste.setFont(new Font("Courier New", Font.PLAIN, 10));
				guiLawsToWaste.setMinimum(2);
				guiLawsToWaste.setEnabled(guiWorldHop.isSelected());
				guiLawsToWaste.addChangeListener(new ChangeListener() {
					public void stateChanged(final ChangeEvent e) {
						guiLawsToWasteStateChanged(e);
					}
				});
				mainPanel.add(guiLawsToWaste);
				guiLawsToWaste.setBounds(15, 110, 210, 80);

				HPPercentToEat = new JSlider();
				mainPanel.add(HPPercentToEat);
				HPPercentToEat.setMajorTickSpacing(10);
				HPPercentToEat.setMinimum(50);
				HPPercentToEat.setMinorTickSpacing(5);
				HPPercentToEat.setPaintLabels(true);
				HPPercentToEat.setPaintTicks(true);
				HPPercentToEat.setSnapToTicks(true);
				HPPercentToEat.setValue(75);
				HPPercentToEat.setEnabled(foodID > 0);
				HPPercentToEat.setBorder(new TitledBorder("Eat at "
						+ HPPercentToEat.getValue() + "% HP"));
				HPPercentToEat.setFont(new Font("Arial", Font.PLAIN, 12));
				HPPercentToEat.setToolTipText("Adjust to the HP% you wish to eat at.");
				HPPercentToEat.addChangeListener(new ChangeListener() {
					public void stateChanged(final ChangeEvent e) {
						HPPercentToEatStateChanged(e);
					}
				});
				HPPercentToEat.setBounds(12, 213, 460, 80);

				// ---- guiPlayerCountToHop ----
				guiPlayerCountToHop.setMaximum(5);
				guiPlayerCountToHop.setMinorTickSpacing(1);
				guiPlayerCountToHop.setPaintLabels(true);
				guiPlayerCountToHop.setPaintTicks(true);
				guiPlayerCountToHop.setSnapToTicks(true);
				guiPlayerCountToHop.setValue(3);
				guiPlayerCountToHop.setMajorTickSpacing(1);
				guiPlayerCountToHop.setBorder(new TitledBorder(null, "Hop if "
						+ guiPlayerCountToHop.getValue() + " others in Temple", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Courier New", Font.PLAIN, 12)));
				guiPlayerCountToHop.setFont(new Font("Courier New", Font.PLAIN, 10));
				guiPlayerCountToHop.setMinimum(1);
				guiPlayerCountToHop.setEnabled(guiWorldHop.isSelected());
				guiPlayerCountToHop.addChangeListener(new ChangeListener() {
					public void stateChanged(final ChangeEvent e) {
						guiPlayerCountToHopStateChanged(e);
					}
				});
				mainPanel.add(guiPlayerCountToHop);
				guiPlayerCountToHop.setBounds(260, 110, 210, 80);

				{ // compute preferred size
					final Dimension preferredSize = new Dimension();
					for (int i = 0; i < mainPanel.getComponentCount(); i++) {
						final Rectangle bounds = mainPanel.getComponent(i).getBounds();
						preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
						preferredSize.height = Math.max(bounds.y
								+ bounds.height, preferredSize.height);
					}
					final Insets insets = mainPanel.getInsets();
					preferredSize.width += insets.right;
					preferredSize.height += insets.bottom;
					mainPanel.setMinimumSize(preferredSize);
				}
			}
			contentPane.add(mainPanel);
			mainPanel.setBounds(0, 0, 484, 370);

			{ // compute preferred size
				final Dimension preferredSize = new Dimension();
				for (int i = 0; i < contentPane.getComponentCount(); i++) {
					final Rectangle bounds = contentPane.getComponent(i).getBounds();
					preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
					preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
				}
				final Insets insets = contentPane.getInsets();
				preferredSize.width += insets.right;
				preferredSize.height += insets.bottom;
				contentPane.setMinimumSize(preferredSize);
				contentPane.setPreferredSize(preferredSize);
			}
			this.setSize(494, 400);
			setLocationRelativeTo(getOwner());
		}

		private void runButtonPressed(final ActionEvent e) {
			runButtonPressed = true;
			worldHopping = guiWorldHop.isSelected();
			if (worldHopping) {
				lawsToWaste = guiLawsToWaste.getValue();
				maxPlayers = guiPlayerCountToHop.getValue();
			}
			checkForUpdates = guiCheckForUpdates.isSelected();
			restForTheWicked = guiRestForTheWicked.isSelected();
			if (foodID > 0) {
				canHazCheezBurger = HPPercentToEat.getValue();
			}
			gui.dispose();
			return;
		}

		private void thisWindowClosing(final WindowEvent e) {
			if (!runButtonPressed) {
				guiExit = true;
				return;
			}
			gui.dispose();
			return;

		}

		private void worldHopItemStateChanged(final ItemEvent e) {
			guiLawsToWaste.setEnabled(guiWorldHop.isSelected());
			guiPlayerCountToHop.setEnabled(guiWorldHop.isSelected());
		}
	}

	private enum STATE {
		SNATCH, TELEPORT, WALK_TO_BANK, BANK, WALK_TO_TEMPLE, WALK_TO_TILE, SLEEP
	};

	private boolean isJar = false;

	public File PRICE_FILE = new File(new File(Configuration.Paths.getScriptCacheDirectory()), "WGGEPrices.txt");

	private static final int wineID = 245, lawID = 563, waterID = 555,
			airStaff = 1381, caitlinsStaff = 15598;

	private static final DecimalFormat whole = new DecimalFormat("####");

	private static final RSArea zammyTempleArea = new RSArea(2930, 3513, 2943, 3518),
			bankArea = new RSArea(2949, 3368, 2943, 3371),
			restArea = new RSArea(new RSTile[] { new RSTile(2954, 3396),
					new RSTile(2974, 3396), new RSTile(2974, 3416),
					new RSTile(2957, 3418) }),
			landingArea = new RSArea(2661, 3396, 2969, 3386);

	private static final RSTile[] waitSpots = { new RSTile(2932, 3515),
			new RSTile(2932, 3514), new RSTile(2931, 3514) };

	private static final RSTile[] templePath = { new RSTile(2946, 3371),
			new RSTile(2948, 3376), new RSTile(2950, 3380),
			new RSTile(2954, 3382), new RSTile(2960, 3384),
			new RSTile(2962, 3389), new RSTile(2965, 3394),
			new RSTile(2965, 3400), new RSTile(2963, 3403),
			new RSTile(2965, 3407), new RSTile(2964, 3411),
			new RSTile(2959, 3416), new RSTile(2955, 3419),
			new RSTile(2951, 3421), new RSTile(2948, 3426),
			new RSTile(2949, 3430), new RSTile(2948, 3434),
			new RSTile(2948, 3440), new RSTile(2947, 3445),
			new RSTile(2946, 3449), new RSTile(2945, 3453),
			new RSTile(2945, 3459), new RSTile(2945, 3464),
			new RSTile(2945, 3468), new RSTile(2945, 3472),
			new RSTile(2942, 3477), new RSTile(2942, 3482),
			new RSTile(2942, 3487), new RSTile(2942, 3491),
			new RSTile(2942, 3495), new RSTile(2942, 3499),
			new RSTile(2941, 3504), new RSTile(2941, 3508),
			new RSTile(2941, 3512), new RSTile(2942, 3516),
			new RSTile(2936, 3516), new RSTile(2933, 3515) }, bankPath = {
			new RSTile(2961, 3381), new RSTile(2955, 3381),
			new RSTile(2950, 3377), new RSTile(2947, 3376),
			new RSTile(2946, 3368) };

	RSTilePath pathToTemple, pathToBank;
	RSTile me, waitSpot = waitSpots[random(0, 2)];

	RSTile hover = new RSTile(2931, 3515);

	RSGroundItem wine;
	Rectangle paintToggle = new Rectangle(390, 343, 120, 15);

	Rectangle killMeNow = new Rectangle(390, 360, 120, 15);

	private long startTime = 0;

	private final Double version = BytesWineGrabber.class.getAnnotation(ScriptManifest.class).version();
	private String playerName;

	private boolean isMember;

	private final String version$ = version.toString();

	private String food$ = "", status$ = "Starting Up...",
			buttonText$ = "Hide Paint";

	private final String killButton$ = "Stop Script!";

	private int winePrice = 0, wineTaken = 0, wineInInv = 0, lawPrice = 0,
			lawsUsed = 0, lawsInInv = 0, lawsWasted = 0, lawsToGet = 0,
			misses = 0, watersInInv = 0, bankTrips = 0;

	private final int minstrelID = 5442;

	private int foodID = 0, foodToGet = 0, canHazCheezBurger = 0;

	private final int maxPastWorlds = 10;

	private int MAXping = 150, MAXpop = 1000, currentWorld = 0, newWorld = 0,
			hopped = 0, skillTotal = 0;
	private int[] startXP;

	private final int pastWorld[] = new int[maxPastWorlds];

	private boolean showPaint = true, goToBank = false, lostDuel = false,
			hopping = false, killScript = false;
	/* Warm GUI shit */
	GUI gui;

	private boolean runButtonPressed = false, worldHopping = false,
			checkForUpdates = false, restForTheWicked = false, guiExit = false;

	private final boolean testHopping = false; // change to true to do nothing
												// but hop worlds...for testing
												// purposes only!

	private int lawsToWaste = 0, maxPlayers = 0;

	private void antiBan() {
		switch (random(0, 7)) {
		case 1:
			camera.moveRandomly(random(500, 1000));
			sleep(650, 800);
			break;
		case 2:
			camera.moveRandomly(random(500, 1000));
			sleep(750, 1000);
			break;
		case 3:
			mouse.moveOffScreen();
			sleep(1000, 1500);
			break;
		case 4:
			mouse.moveOffScreen();
			sleep(600, 700);
			break;
		case 5:
			mouse.moveRandomly(20, 100);
			sleep(400, 800);
			break;
		case 6:
			mouse.moveSlightly();
			sleep(500, 750);
			break;
		case 7:
			camera.setNorth();
			camera.setPitch(true);
			sleep(1000, 1500);
			break;
		}
	}

	private STATE doWhat() {

		if (zammyTempleArea.contains(me)) {

			if (players.getMyPlayer().getHPPercent() < 50 || goToBank) {
				status$ = "Teleporting to Falador";
				return STATE.TELEPORT;
			}

			if (!me.equals(waitSpot)) {
				status$ = "Moving to wait spot";
				return STATE.WALK_TO_TILE;
			}
			status$ = "Waiting for Wine...";
			return STATE.SNATCH;
		}

		if (bankArea.contains(me)) {
			if (players.getMyPlayer().isIdle() && goToBank || wineInInv > 0) {
				status$ = "Banking";
				return STATE.BANK;
			}
		}

		if ((goToBank || players.getMyPlayer().getHPPercent() < 51)
				&& !bankArea.contains(me)) {
			status$ = "Walking to Bank";
			return STATE.WALK_TO_BANK;
		} else {
			status$ = "Goin' to Church....";
			return STATE.WALK_TO_TEMPLE;
		}
	}

	private void download(final String address, final String localFileName) {
		OutputStream out = null;
		URLConnection conn;
		InputStream in = null;
		try {
			final URL url = new URL(address);

			out = new BufferedOutputStream(new FileOutputStream(localFileName));
			conn = url.openConnection();
			in = conn.getInputStream();

			final byte[] buffer = new byte[1024];
			int numRead;
			while ((numRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, numRead);
			}
		} catch (final Exception exception) {
			log("Downloading failed!");
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
			} catch (final IOException ioe) {
				log("Downloading failed!");
			}
		}
	}

	/*
	 * Subroutines
	 */

	private void eatFood() {
		if (inventory.contains(foodID)) {
			inventory.getItem(foodID).doClick(true);
		}
	}

	private void findFoodID() {
		final RSItem[] is = inventory.getItems();
		for (final RSItem i : is) {
			if (i.getComponent().getActions() == null
					|| i.getComponent().getActions()[0] == null) {
				continue;
			}
			if (i.getComponent().getActions()[0].contains("Eat")) {
				foodID = i.getID();
				food$ = i.getName();
			}
		}
		return;
	}

	private String format(final long time, final boolean seconds) {
		if (time <= 0) {
			return "--:--:--";
		}
		final StringBuilder t = new StringBuilder();
		final long TotalSec = time / 1000;
		final long TotalMin = TotalSec / 60;
		final long TotalHour = TotalMin / 60;
		final int second = (int) TotalSec % 60;
		final int minute = (int) TotalMin % 60;
		final int hour = (int) TotalHour;
		if (hour < 10) {
			t.append("0");
		}
		t.append(hour);
		t.append(":");
		if (minute < 10) {
			t.append("0");
		}
		t.append(minute);
		if (seconds) {
			t.append(":");
			if (second < 10) {
				t.append("0");
			}
			t.append(second);
		} else {
			t.append(":00");
		}
		return t.toString();
	}

	private String getDate() {
		final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		final Date date = new Date();
		return dateFormat.format(date);
	}

	private RSComponent getWorldComponent(final int world,
			final RSComponent[] WorldNumberComponents,
			final RSComponent[] WorldComponents) {
		for (int i = 0; i < WorldNumberComponents.length; i++) {
			int w = -1;
			try {
				w = Integer.parseInt(WorldNumberComponents[i].getText());
			} catch (final Exception e) {
				w = -1;
			}
			if (w != -1 && w == world) {
				return WorldComponents[i];
			}
		}
		return null;
	}

	private boolean isPastWorld(final int world) {
		for (final int element : pastWorld) {
			if (element == world) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int loop() {

		if (killScript) {
			return -1;
		}

		mouse.setSpeed(random(4, 8));

		me = players.getMyPlayer().getLocation();
		RSTile dest = walking.getDestination();
		if (dest == null) {
			dest = me;
		}

		if (!bank.isOpen()) {
			wineInInv = inventory.getCount(wineID);
			if (inventory.contains(lawID)) {
				lawsInInv = inventory.getItem(lawID).getStackSize();
			} else {
				lawsInInv = 0;
			}
			if (inventory.contains(waterID)) {
				watersInInv = inventory.getItem(waterID).getStackSize();
			} else {
				watersInInv = 0;
			}
			goToBank = inventory.isFull() || landingArea.contains(me)
					&& wineInInv > 1 || lawsInInv <= 1 || watersInInv == 0;
		}

		if (restForTheWicked && restArea.contains(dest)
				&& walking.getEnergy() < 90) {
			final RSNPC minstrel = npcs.getNearest(minstrelID);
			if (minstrel != null) {
				final RSTile minstrelLocation = minstrel.getLocation();
				walking.walkTileMM(minstrelLocation);
				waitPlayerMoving();
				if (calc.distanceTo(minstrelLocation) < 4) {
					walking.rest();
					sleep(1250, 1500);
				}
			}
		}

		if (!walking.isRunEnabled() && walking.getEnergy() >= 20
				&& players.getMyPlayer().isMoving()) {
			walking.setRun(true);
			return random(750, 1000);
		}

		if (foodID > 0
				&& players.getMyPlayer().getHPPercent() < canHazCheezBurger) {
			if (inventory.contains(foodID)) {
				eatFood();
				return 0;
			}
			return 500;
		}

		switch (doWhat()) {

		case SNATCH:
			if (testHopping || worldHopping && misses >= lawsToWaste) {
				misses = 0;
				worldHop(isMember, MAXping, MAXpop);
				hopping = false;
				return 100;
			}

			if (camera.getPitch() != 100) {
				camera.setPitch(100);
				sleep(900, 1000);
			}
			while (!magic.isSpellSelected()) {
				magic.castSpell(magic.SPELL_TELEKINETIC_GRAB);
				sleep(250);
			}

			mouse.move(calc.tileToScreen(hover, -500));
			mouse.setSpeed(1);
			for (int i = 0; i < 1000; i++) {
				final RSGroundItem wine = groundItems.getNearest(wineID);
				if (wine != null && wine.isOnScreen()) {
					mouse.click(calc.tileToScreen(wine.getLocation(), -500), true);
					sleep(1000, 1200);
					antiBan();
					break;
				}
				if (!waitSpot.equals(players.getMyPlayer().getLocation())
						|| foodID > 0
						&& players.getMyPlayer().getHPPercent() < canHazCheezBurger
						|| players.getMyPlayer().getHPPercent() < 50
						|| killScript) {
					break; // break out if a random occurs...or need food...or
							// need to bail.
				}
				while (!magic.isSpellSelected()) {
					magic.castSpell(magic.SPELL_TELEKINETIC_GRAB);
					sleep(250);
				}
				sleep(25, 26);
			}
			if (foodID > 0
					&& players.getMyPlayer().getHPPercent() < canHazCheezBurger) {
				log("Eating?");
				if (inventory.contains(foodID)) {
					eatFood();
					return 0;
				}
			}
			if (players.getMyPlayer().getHPPercent() < 50) {
				return 0;
			}

			sleep(1000, 1200);
			antiBan();

			if (wineInInv < inventory.getCount(wineID)) {
				wineTaken++;
				if (misses > 0) {
					misses--;
				}
				if (worldHopping && playerCount() > maxPlayers) {
					if (lostDuel) {
						lostDuel = false;
						worldHop(isMember, MAXping, MAXpop);
					}
					lostDuel = true;
				}
				wineInInv = inventory.getCount(wineID);
			} else {
				misses++;
			}
			try {
				if (lawsInInv > inventory.getItem(lawID).getStackSize()) {
					lawsUsed++;
				}
			} catch (final NullPointerException e) {
			}

			return 50;

		case TELEPORT:
			if (magic.castSpell(magic.SPELL_FALADOR_TELEPORT)) {
				sleep(5500, 6000);
				return random(3000, 3500);
			}
			return 5;

		case WALK_TO_BANK:
			pathToBank.traverse();
			return 50;

		case BANK:
			if (!bank.isOpen()) {
				if (foodID > 0) {
					log("We are Eating...");
					if (!inventory.contains(foodID)) {
						log("Have NO food!");
						foodToGet = 4;
					} else {
						log("Food in Inventory: " + inventory.getCount(foodID));
						if (inventory.contains(foodID)) {
							foodToGet = 4 - inventory.getCount(foodID);
						}
						log("Need " + foodToGet + " food.");
					}
				}
				bank.open();
				return 500;
			}

			if (inventory.contains(wineID)) {
				bank.deposit(wineID, 0);
				return random(2000, 2200);
			}

			int x = 0;

			if (foodToGet > 0 && bank.getItem(foodID) != null) {
				x = bank.getItem(foodID).getStackSize();
			}

			if (x > foodToGet) {
				bank.withdraw(foodID, foodToGet);
				sleep(4000, 4200);
				if (x > bank.getItem(foodID).getStackSize()) {
					foodToGet = 0;
				} else {
					return 50;
				}
			}

			if (bank.getItem(lawID) != null) {
				x = bank.getItem(lawID).getStackSize();
				lawsToGet = 100 - lawsInInv;
			} else {
				lawsToGet = 0;
			}

			if (lawsInInv < 100 && x >= lawsToGet) {
				bank.withdraw(lawID, lawsToGet);
				sleep(4000, 4200);
				if (bank.getItem(lawID) == null
						|| bank.getItem(lawID).getStackSize() < x) {
					lawsInInv = lawsInInv + lawsToGet;
				} else {
					return 50;
				}
			}

			if (!inventory.contains(wineID)) {
				bankTrips++;
			}

			if (!bank.close()) {
				bank.close();
			}
			sleep(1000, 1200);
			antiBan();

			if (inventory.getCount(lawID) == 0
					|| inventory.getCount(waterID) == 0 || foodToGet > 0) {
				log("Out of Laws/Waters/Food, Quitting.");
				env.saveScreenshot(true);
				return -1;
			}

			return 50;

		case WALK_TO_TEMPLE:
			pathToTemple.traverse();
			return 50;

		case WALK_TO_TILE:
			/* first check for too many ppl in Temple */
			if (playerCount() > maxPlayers && worldHopping) {
				worldHop(isMember);
			}
			tiles.doAction(calc.getTileOnScreen(waitSpot), "here");
			// walking.walkTileOnScreen(waitSpot);

			if (walking.getDestination() != null
					&& !zammyTempleArea.contains(walking.getDestination())) {
				walking.walkTileOnScreen(players.getMyPlayer().getLocation());
			}
			waitPlayerMoving();
			sleep(1000, 1200);
			antiBan();
			return 50;

		case SLEEP:
			log("Houston, we have a problem.");
		}
		return 1;
	}

	@Override
	public void messageReceived(final MessageEvent e) {
		e.getMessage().toLowerCase();

		if (e.getID() == MessageEvent.MESSAGE_SERVER) {

		}

	}

	public void mouseClicked(final MouseEvent e) {
		final Point q = e.getPoint();
		if (paintToggle.contains(q)) {
			showPaint = !showPaint;
			if (showPaint) {
				buttonText$ = "Hide Paint";
			} else {
				buttonText$ = "Show Paint";
			}
		}

		if (killMeNow.contains(q)) {
			killScript = true;
		}
	}

	public void mouseEntered(final MouseEvent e) {
	}

	public void mouseExited(final MouseEvent e) {
	}

	public void mousePressed(final MouseEvent e) {
	}

	public void mouseReleased(final MouseEvent e) {
	}

	@Override
	public void onRepaint(final Graphics render) {
		final Graphics2D g = (Graphics2D) render;
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		final int x = 7;
		int y = 390, z = 343;
		final int len = 520, fntSize = 11, spcng = 12;

		if (!game.isFixed()) {
			y = game.getHeight() - 113;
			z = y - 47;
			paintToggle = new Rectangle(390, z, 120, 15);
			killMeNow = new Rectangle(390, z + 17, 120, 15);
		}
		if (showPaint) {

			final long runTime = System.currentTimeMillis() - startTime;
			double gph, wph = 0;
			lawsWasted = lawsUsed - wineTaken - bankTrips;

			// render "+" over mouse.
			final Point m = mouse.getLocation();
			g.drawLine((int) m.getX() - 3, (int) m.getY() + 3, (int) m.getX() + 3, (int) m.getY() - 3);
			g.drawLine((int) m.getX() - 3, (int) m.getY() - 3, (int) m.getX() + 3, (int) m.getY() + 3);
			g.setFont(new Font("Arial", Font.PLAIN, fntSize));
			g.setColor(new Color(255, 0, 0, 220));

			wph = wineTaken * 3600000D
					/ (System.currentTimeMillis() - startTime);
			if (wph < 0) {
				wph = 0;
			}
			int ttlWine = winePrice * wineTaken;
			if (ttlWine < 0) {
				ttlWine = 0;
			}
			gph = (ttlWine - lawPrice * lawsUsed) * 3600000D
					/ (System.currentTimeMillis() - startTime);

			g.setColor(new Color(0, 0, 0, 220));
			if (killScript) {
				g.setColor(new Color(128, 0, 0, 180));
			}
			g.fillRoundRect(x - 6, y - 50, len, spcng * 11 + 8, 5, 5);
			g.setColor(Color.WHITE);
			g.drawRoundRect(x - 6, y - 50, len, spcng * 11 + 8, 5, 5);
			g.drawString("Byte's Zammy Wine Grabber: " + format(runTime, true)
					+ " v" + version$ + " Mr. Byte", x, (y - spcng * 3));
			g.setColor(Color.WHITE);
			g.drawString("Wines Grabbed: " + wineTaken + " Laws Used: "
					+ lawsUsed + "  Law Wasteage: " + lawsWasted
					+ "   Laws in Inventory: " + lawsInInv, x, (y - spcng));

			g.drawString("Wine Snatched/hr: " + whole.format(wph)
					+ "     Gold Per Hour: " + whole.format(gph)
					+ "    Wine price: " + winePrice
					+ "    Cost for Law Rune: " + lawPrice, x, y + spcng * 6);
			g.drawString("Status: " + status$, x + len - (status$.length() + 8)
					* 6, y + spcng * 7);
			if (worldHopping) {
				final int c = 255 / lawsToWaste;
				g.setColor(new Color(255, 255 - c * misses, 255 - c * misses));
				g.drawString("   Hopping in " + (lawsToWaste - misses)
						+ " misses.           Hopped " + hopped + " times.", x, y
						+ spcng * 7);
			} else {
				g.setColor(Color.WHITE);
				g.drawString("NOT World Hopping...", x, y + spcng * 7);
			}
			g.setFont(new Font("Arial", Font.PLAIN, fntSize));
			if (!game.isLoggedIn()) {
				y += spcng * 2;
				g.setFont(new Font("Arial", Font.PLAIN, fntSize * 2));
				if (hopping) {
					g.drawString("...Hopping Worlds...", x + len / 3, y);
				} else {
					g.drawString("In Lobby or Logged Out.", x + len / 3 - 2, y);
				}

			}
		}
		g.setFont(new Font("Arial", Font.PLAIN, fntSize));
		g.setColor(new Color(255, 0, 0, 220));
		g.fillRoundRect(400, z, 120, 15, 7, 7);
		g.setColor(Color.WHITE);
		g.drawRoundRect(400, z, 120, 15, 7, 7);
		g.drawString(buttonText$, 435, z + 12);
		g.setColor(new Color(0, 0, 128, 220));
		g.fillRoundRect(400, z + 17, 120, 15, 7, 7);
		g.setColor(Color.WHITE);
		g.drawRoundRect(400, z + 17, 120, 15, 7, 7);
		g.drawString(killButton$, 435, z + 29);

	}

	public boolean onStart() {

		final String className = this.getClass().getName().replace('.', '/');
		final String classJar = this.getClass().getResource("/" + className
				+ ".class").toString();
		if (classJar.startsWith("jar:")) {
			log("*** running from jar!");
			isJar = true;
		}

		findFoodID();
		if (foodID != 0) {
			log("Eating " + food$);
		}

		gui = new GUI();
		gui.setVisible(true);
		while (gui.isVisible()) {
			sleep(50);
		}
		if (guiExit) {
			return false;
		}

		pathToTemple = walking.newTilePath(templePath);
		pathToBank = walking.newTilePath(bankPath);
		final int x = random(0, 2);
		waitSpot = waitSpots[x];
		log("WaitSpot# " + x);

		if (!inventory.contains(waterID)
				|| equipment.getItem(equipment.WEAPON).getID() != airStaff
				&& equipment.getItem(equipment.WEAPON).getID() != caitlinsStaff) {
			log("You need Water runes, and an equipped Air/Caitlin's Staff to use the script.");
			log("It helps to have laws in inventory as well, but they can be in the bank.");
			return false;
		}

		playerName = account.getName();
		isMember = AccountManager.isMember(playerName);

		wineInInv = inventory.getCount(wineID);
		goToBank = inventory.isFull();
		me = players.getMyPlayer().getLocation();

		if (inventory.getItem(lawID) != null) {
			lawsInInv = inventory.getItem(lawID).getStackSize();
		}

		watersInInv = inventory.getItem(waterID).getStackSize();

		if (!readPrices()) {
			winePrice = grandExchange.lookup(wineID).getGuidePrice();
			lawPrice = grandExchange.lookup(lawID).getGuidePrice();
			writePrices();
		}

		startXP = new int[7];
		for (int i = 0; i < 25; i++) {
			if (i < 7) {
				startXP[i] = skills.getCurrentExp(i);
			}
			skillTotal += skills.getRealLevel(i);
		}
		log(skillTotal + " total level");
		startTime = System.currentTimeMillis();
		log("XP array loaded, Start Time set.");
		log(" ");
		log.severe("Note: in the event of PowerBot being down, you can find info about");
		log.severe("this script at http://www.LetTheSmokeOut.com. In addition, all updates");
		log.severe("are hosted there as well.");

		/********************************************
		 * ATTENTION: To disable automatic updating * Change the next line to
		 * "return true;" *
		 ********************************************/
		if (checkForUpdates) {
			return updater(); // checks for, downloads and compiles updates.
		}
		return true;
	}

	private int pingHost(final String host, final int timeout) {
		long start = -1;
		long end = -1;
		int total = -1;
		final int defaultPort = 80;
		final Socket theSock = new Socket();
		try {
			final InetAddress addr = InetAddress.getByName(host);
			final SocketAddress sockaddr = new InetSocketAddress(addr, defaultPort);
			start = System.currentTimeMillis();
			theSock.connect(sockaddr, timeout);
			end = System.currentTimeMillis();
		} catch (final Exception e) {
			start = -1;
			end = -1;
		} finally {
			if (theSock != null) {
				try {
					theSock.close();
				} catch (final IOException e) {
				}
				if (start != -1 && end != -1) {
					total = (int) (end - start);
					log(host + "'s ping delay is " + total + "ms.");
				} else {
					log("Connection timed out or unable to connect to host: "
							+ host);
				}
			}
		}
		return total; // returns -1 if timeout
	}

	private int playerCount() {
		final RSPlayer[] gang = players.getAll(new Filter<RSPlayer>() {
			public boolean accept(final RSPlayer p) {
				return zammyTempleArea.contains(p.getLocation())
						&& !p.equals(players.getMyPlayer());
			}
		});
		int x = 0;
		if (gang != null) {
			x = gang.length;
		}
		log(x + " players in temple...");
		return x;
	}

	private boolean readPrices() {
		try {

			final BufferedReader in = new BufferedReader(new FileReader(PRICE_FILE));
			String line;
			String[] opts = {};

			while ((line = in.readLine()) != null) {
				if (line.contains(":")) {
					opts = line.split(":");
					if (!opts[0].equals(getDate())) {
						log("Old prices in file...getting latest prices from Grand Exchange...");
						return false; // need new prices, different date.
					}
					if (Integer.parseInt(opts[1]) == wineID) {
						winePrice = Integer.parseInt(opts[2]);
					}
					if (Integer.parseInt(opts[1]) == lawID) {
						lawPrice = Integer.parseInt(opts[2]);
					}
				}
			}
			in.close();
		} catch (final IOException ignored) {
			return false;
		}

		return true;
	}

	/**
	 * Download method from RSBot UpdateUtil by TheShadow Copied to here because
	 * it wouldn't let me call it from here...some static thing (shrug)
	 **/

	public boolean updater() {
		final Pattern UPDATER_VERSION_PATTERN = Pattern.compile("version\\s*=\\s*([0-9.]+)");
		final String scriptName = BytesWineGrabber.class.getAnnotation(ScriptManifest.class).name();

		final String scriptHost = "http://letthesmokeout.com/RSBot/";

		final String className = this.getClass().getName();

		final String javaName = className + ".java";
		final String jarName = className + ".jar";
		final String javaURL = scriptHost + javaName;
		final String jarURL = scriptHost + jarName;
		final String localJavaName = Configuration.Paths.getScriptsSourcesDirectory()
				+ File.separator + javaName;
		final String localJarName = Configuration.Paths.getScriptsPrecompiledDirectory()
				+ File.separator + jarName;
		double revision = -1.0;

		String dialog$ = "An update is available, do you wish to download it now?";
		if (isJar) {
			dialog$ = dialog$
					+ " It will be downloaded into your Precompiled folder.";
		} else {
			dialog$ = dialog$ + "  It will be saved to your scripts folder.";
		}

		if (pingHost("letthesmokeout.com", 150) == -1) {
			log("LetTheSmokeOut.com is currently down, please check for updates later.");
			return true;
		}

		try {
			/*
			 * Get the current version from the Script Manifest annotation
			 * defined at the top of script's class
			 */
			final URL url = new URL(javaURL);

			/* Open a stream to the newest script file hosted on server */
			final BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			String line, lines = "";
			Matcher m;

			/*
			 * Look for the "version = x.x" string in the newer file to figure
			 * out the newest version number
			 */

			while ((line = in.readLine()) != null) {
				lines += line + "\n";
				if ((m = UPDATER_VERSION_PATTERN.matcher(line)).find()) {
					revision = Double.parseDouble(m.group(1));
					break;
				}
			}
			/* Check if the updater was unable to read the newest version number */
			if (revision < 0) {
				in.close();
				log("Unable to find the new version number. Update failed");
				return false;
			}
			if (in != null) {
				in.close();
			}

			if (version < revision) {
				if (JOptionPane.showConfirmDialog(null, dialog$, scriptName
						+ " v" + revision + " is available!", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					if (isJar) {
						download(jarURL, localJarName);
						log.severe("New .jar downloaded. Please restart RSBot, or the new .jar will not appear!");
						return false;
					} else {
						download(javaURL, localJavaName);

						log.severe("Exiting, please review code, recompile scripts and restart.");
						return false;
					}
				} else { // update declined...
					log.severe("Updater: New version available. Download at "
							+ javaURL);
					log.severe("Updater: and save it into " + localJavaName);
				}
			}

			if (version == revision) {
				log("Updater: You have the newest available version!");
			}
			if (version > revision) {
				log.severe("Updater: You have a newer version than available!");
			}
		} catch (final IOException e) {
			log.severe("Updater:  Problem getting version. - go to LetTheSmokeOut.com for details on updating.");
		}
		return true;
	}

	private void waitPlayerMoving() {
		while (players.getMyPlayer().isMoving()
				|| walking.getDestination() != null) {
			sleep(10, 15);
		}
	}

	/**
	 * Hops to the first visible filtered world on the list by jtryba avoids the
	 * last x worlds you've been to since starting the script credits to MrByte
	 * for his help
	 * 
	 * @param members
	 *            <tt>true</tt> if player should hop to a members world.
	 * @return void
	 * 
	 */

	public void worldHop(final boolean members) {
		worldHop(members, MAXping);
	}

	public void worldHop(final boolean members, final int maxping) {
		worldHop(members, maxping, MAXpop);
	}

	public void worldHop(final boolean members, final int maxping,
			final int maxpop) {

		final int LOBBY_PARENT = 906;
		final int WORLD_SELECT_TAB_PARENT = 910;
		final int WORLD_SELECT_COM = 64;
		final int WORLD_NUMBER_COM = 69;
		final int WORLD_POPULATION_COM = 71;
		final int WORLD_ACTIVITY = 72;
		final int WORLD_TYPE_COM = 74;
		final int WORLD_SELECT_BUTTON_COM = 189;
		final int WORLD_SELECT_BUTTON_BG_COM = 12;
		final int WORLD_SELECT_TAB_ACTIVE = 4671;
		final int WORLD_FULL_BACK_BUTTON_COM = 233;
		final int CURRENT_WORLD_COM = 11;
		final int SORT_POPULATION_BUTTON_PARENT = 30;
		final int SORT_PING_BUTTON_PARENT = 45;
		final int SORT_LOOTSHARE_BUTTON_PARENT = 47;
		final int SORT_TYPE_BUTTON_PARENT = 49;
		final int SORT_ACTIVITY_BUTTON_PARENT = 52;
		final int SORT_WORLD_BUTTON_PARENT = 55;
		final int SCROLL_BAR_PARENT = 86;
		final int HIGH_RISK_WARN_PARENT = 93;
		final int PLAY_BUTTON_COM = 171;
		final int RETURN_TEXT_COM = 224;
		final int SUBSCRIBE_BACK_BUTTON_COM = 233;
		final int CONNECT_ERROR_BACK_BUTTON_COM = 42;
		final int BACK_BUTTON = 231;
		final int SAFETY_TIMEOUT = 20 * 1000;
		long safety = 0;
		int ping = 0, adjping = 2000, adjpop = 2000;

		boolean popadj = false, pingadj = false;
		final boolean verbose = false;

		log("Hopping worlds");
		status$ = "Hopping worlds";
		hopping = true;

		// logout
		safety = System.currentTimeMillis() + SAFETY_TIMEOUT;
		while (game.isLoggedIn() && System.currentTimeMillis() < safety) {
			if (game.logout(true)) {
				while (game.getClientState() != 7) {
					sleep(random(200, 500));
				}
			}
		}

		status$ = "Selecting new world";
		sleep(random(1500, 2000));

		if (verbose) {
			log("click world select tab (if needed)");
		}
		safety = System.currentTimeMillis() + SAFETY_TIMEOUT;
		final RSComponent worldSelectTab = interfaces.getComponent(LOBBY_PARENT, WORLD_SELECT_BUTTON_BG_COM);
		while (worldSelectTab.getBackgroundColor() != WORLD_SELECT_TAB_ACTIVE
				&& System.currentTimeMillis() < safety) {
			if (interfaces.getComponent(LOBBY_PARENT, WORLD_SELECT_BUTTON_COM).doClick()) {
				sleep(random(1500, 2000));
			}
		}

		if (verbose) {
			log("get current world");
		}
		final String cW = interfaces.getComponent(WORLD_SELECT_TAB_PARENT, CURRENT_WORLD_COM).getText();
		final String[] cWS = cW.split(" ");
		try {
			currentWorld = Integer.parseInt(cWS[1]);
		} catch (final NullPointerException e) {
			if (verbose) {
				log("Error getting current world, returning...");
			}
			return;
		}
		newWorld = currentWorld;

		if (verbose) {
			log("randomly sort worlds");
		}
		if (random(0, 10) < 3) {
			RSComponent com = null;
			switch (random(0, 4)) {
			case 0:
				if (verbose) {
					log("population");
				}
				com = interfaces.getComponent(WORLD_SELECT_TAB_PARENT, SORT_POPULATION_BUTTON_PARENT).getComponent(random(0, 2));
				break;
			case 1:
				if (verbose) {
					log(" ping");
				}
				com = interfaces.getComponent(WORLD_SELECT_TAB_PARENT, SORT_PING_BUTTON_PARENT).getComponent(random(0, 2));
				break;
			case 2:
				if (verbose) {
					log("loot share");
				}
				com = interfaces.getComponent(WORLD_SELECT_TAB_PARENT, SORT_LOOTSHARE_BUTTON_PARENT).getComponent(random(0, 2));
				break;
			case 3:
				if (verbose) {
					log("activity");
				}
				com = interfaces.getComponent(WORLD_SELECT_TAB_PARENT, SORT_ACTIVITY_BUTTON_PARENT).getComponent(random(0, 2));
				break;
			case 4:
				if (verbose) {
					log("world");
				}
				com = interfaces.getComponent(WORLD_SELECT_TAB_PARENT, SORT_WORLD_BUTTON_PARENT).getComponent(random(0, 2));
				break;
			}
			if (com != null) {
				if (com.doClick()) {
					sleep(random(1250, 1500));
				}
			}
		}

		if (verbose) {
			log("sort by ptp/ftp");
		}
		status$ = "Sorting by type";
		if (interfaces.getComponent(WORLD_SELECT_TAB_PARENT, SORT_TYPE_BUTTON_PARENT).getComponent(members ? 0
				: 1).doClick()) {
			sleep(random(1000, 1500));
		}

		/*-PICK-NEXT-WORLD-*/
		safety = System.currentTimeMillis() + SAFETY_TIMEOUT;
		while (currentWorld == newWorld && game.getClientState() != 10
				&& System.currentTimeMillis() < safety) {
			status$ = "Selecting next world";
			RSComponent worldToHop = null;
			RSComponent[] comWorldNumber = interfaces.getComponent(WORLD_SELECT_TAB_PARENT, WORLD_NUMBER_COM).getComponents();
			RSComponent[] comWorldSelect = interfaces.getComponent(WORLD_SELECT_TAB_PARENT, WORLD_SELECT_COM).getComponents();
			int world = 0;
			int pop = 2000;
			boolean member = false;
			while (worldToHop == null
					&& System.currentTimeMillis() < safety + 2000) {
				for (int i = 0; i < comWorldNumber.length - 1; i++) {
					comWorldSelect = interfaces.getComponent(WORLD_SELECT_TAB_PARENT, WORLD_SELECT_COM).getComponents();
					final RSComponent[] comWorldPopulation = interfaces.getComponent(WORLD_SELECT_TAB_PARENT, WORLD_POPULATION_COM).getComponents();
					final RSComponent[] comWorldType = interfaces.getComponent(WORLD_SELECT_TAB_PARENT, WORLD_TYPE_COM).getComponents();
					comWorldNumber = interfaces.getComponent(WORLD_SELECT_TAB_PARENT, WORLD_NUMBER_COM).getComponents();
					final RSComponent[] comWorldActivity = interfaces.getComponent(WORLD_SELECT_TAB_PARENT, WORLD_ACTIVITY).getComponents();
					try {
						world = Integer.parseInt(comWorldNumber[i].getText());
					} catch (final NumberFormatException nfe) {
						world = 0;
					} catch (final ArrayIndexOutOfBoundsException aie) {
						world = 0;
					}
					try {
						pop = Integer.parseInt(comWorldPopulation[i].getText());
					} catch (final NumberFormatException nfe) {
						pop = 2000;
					} catch (final ArrayIndexOutOfBoundsException aie) {
						pop = 2000;
					}
					try {
						if (comWorldType[i].getText().contains("Members")) {
							member = true;
						}
					} catch (final ArrayIndexOutOfBoundsException aie) {
					}
					String act = "";
					try {
						act = comWorldActivity[i].getText();
					} catch (final Exception e) {
					}
					if (act.toLowerCase().contains("skill")) {
						if (act.contains("1000")) {
							if (skillTotal < 1000) {
								world = 0;
							}
						}
						if (act.contains("1500")) {
							if (skillTotal < 1500) {
								world = 0;
							}
						}
					}

					if (world != 0 && !isPastWorld(world)
							&& world != currentWorld && member == members) {
						ping = pingHost("world" + world + ".runescape.com", maxping + 100);
					} else {
						ping = -1;
					}

					if (world > 0) {
						if (ping > 0 && ping > maxping && ping < adjping) {
							if (verbose) {
								log("world: " + world + "   adjping now: "
										+ adjping);
							}
							adjping = ping;
							pingadj = true;
						}

						if (pop > maxpop && pop < adjpop) {
							if (verbose) {
								log("world: " + world + "   adjpop now: "
										+ adjpop);
							}
							adjpop = pop;
							popadj = true;
						}
					}

					if (world > 0 && !isPastWorld(world)
							&& world != currentWorld && pop < maxpop && pop > 0
							&& ping < maxping && ping > 0 && member == members) {
						if (verbose) {
							log("World: " + world + " selected.");
						}
						worldToHop = comWorldSelect[i];
					}
					if (worldToHop != null) {
						break;
					}
				}
				sleep(random(50, 150));
			}

			if (worldToHop != null) {
				int w = -1;
				String[] split = interfaces.getComponent(910, 8).getText().split(" ");
				try {
					w = Integer.parseInt(split[split.length - 1]);
				} catch (final Exception e) {
					w = -1;
				}
				if (verbose) {
					log("click new world");
				}
				while (w != world && worldToHop != null) {
					worldToHop = getWorldComponent(world, comWorldNumber, comWorldSelect);
					if (worldToHop != null) {
						if (worldToHop.getLocation().y <= 280
								&& worldToHop.getLocation().y >= 0) {
							status$ = "Clicking new world";
							worldToHop.doHover();
							if (menu.contains("Select")) {
								worldToHop.doClick();
							} else {
								mouse.move(mouse.getLocation().x
										+ random(25, 55), mouse.getLocation().y);
								if (menu.contains("Select")) {
									mouse.click(true);
								}
							}
						} else {
							final RSComponent scrollBar = interfaces.getComponent(WORLD_SELECT_TAB_PARENT, SCROLL_BAR_PARENT);
							status$ = "Scrolling to new world";
							if (interfaces.scrollTo(worldToHop, scrollBar)) {
								status$ = "Clicking new world";
								worldToHop.doHover();
								if (menu.contains("Select")) {
									worldToHop.doClick();
								} else {
									mouse.move(mouse.getLocation().x
											+ random(25, 55), mouse.getLocation().y);
									if (menu.contains("Select")) {
										mouse.click(true);
									}
								}
							}
						}
						split = interfaces.getComponent(910, 8).getText().split(" ");
						try {
							w = Integer.parseInt(split[split.length - 1]);
						} catch (final Exception e) {
							w = -1;
						}
					}
				}
				if (verbose) {
					log("get new world");
				}
				sleep(random(1000, 1500));
				final String cW2 = interfaces.getComponent(WORLD_SELECT_TAB_PARENT, CURRENT_WORLD_COM).getText();
				final String[] cWS2 = cW2.split(" ");
				newWorld = Integer.parseInt(cWS2[1]);
			} else {
				if (verbose) {
					log("Called with " + maxping + " " + maxpop);
				}
				if (verbose) {
					log("ping or pop too low. MAXping now: " + adjping
							+ " MAXpop:" + adjpop);
				}
				if (pingadj) {
					MAXping = adjping;
				}
				if (popadj) {
					MAXpop = adjpop;
				}
				worldHop(isMember, MAXping, MAXpop);
				return;
			}
		}
		/*-END-PICK-NEXT-WORLD-*/

		if (verbose) {
			log("set last & current world/s");
		}
		if (currentWorld != newWorld) {
			for (int i = 0; i < pastWorld.length; i++) {
				if (i < pastWorld.length - 1) {
					pastWorld[i] = pastWorld[i + 1];
				} else {
					pastWorld[i] = currentWorld;
				}
				if (verbose) {
					log("i: " + i + " pastWorld[i]: " + pastWorld[i]
							+ "currentWorld: " + currentWorld);
				}
			}
			currentWorld = newWorld;
		}

		safety = System.currentTimeMillis() + SAFETY_TIMEOUT;
		while (game.getClientState() != 10
				&& System.currentTimeMillis() < safety) {
			status$ = "Logging in...";
			if (verbose) {
				log("click play button");
			}
			if (interfaces.getComponent(LOBBY_PARENT, PLAY_BUTTON_COM).doClick()) {
				hopped++;
			}

			if (verbose) {
				log("check for high risk world warning during login");
			}
			safety = System.currentTimeMillis() + SAFETY_TIMEOUT;
			while (game.getClientState() != 10
					&& System.currentTimeMillis() < safety) {
				final RSComponent hrParent = interfaces.getComponent(LOBBY_PARENT, HIGH_RISK_WARN_PARENT);
				if (hrParent.isValid()) {
					final RSComponent LogIn = hrParent.getComponent(random(0, hrParent.getComponents().length));
					if (LogIn != null && LogIn.isValid()) {
						if (mouse.getLocation().getX() < 386
								|| mouse.getLocation().getX() > 504
								|| mouse.getLocation().getY() < 357
								|| mouse.getLocation().getY() > 386) {
							if (LogIn.doHover()) {
								sleep(random(250, 500));
								if (menu.contains("Log In")) {
									if (verbose) {
										log("accept warning / click login");
									}
									mouse.click(true);
									log.warning("This is a high risk wilderness world.");
									sleep(random(250, 500));
								}
							}
						}
					}
				}
				sleep(100);
			}
			if (verbose) {
				log("check for login errors");
			}
			final String returnText = interfaces.getComponent(LOBBY_PARENT, RETURN_TEXT_COM).getText().toLowerCase();
			if (!game.isLoggedIn()) {
				if (returnText.contains("update")) {
					status$ = "Stopping script";
					if (verbose) {
						log("Runescape has been updated, please reload RSBot.");
					}
					stopScript(true);
				}
				if (returnText.contains("disable")) {
					status$ = "Stopping script";
					if (verbose) {
						log("Your account is banned/disabled.");
					}
					stopScript(true);
				}
				if (returnText.contains("error connecting")) {
					status$ = "Stopping script";
					if (verbose) {
						log("Error connecting to runescape.");
					}
					interfaces.getComponent(LOBBY_PARENT, CONNECT_ERROR_BACK_BUTTON_COM).doClick();
					stopScript(true);
				}
				if (returnText.contains("full")) {
					if (verbose) {
						log("World Is Full.");
					}
					interfaces.getComponent(LOBBY_PARENT, WORLD_FULL_BACK_BUTTON_COM).doClick();
					sleep(random(1000, 1500));
					worldHop(members, maxping, maxpop); // try again
				}
				if (returnText.contains("subscribe")) {
					interfaces.getComponent(LOBBY_PARENT, SUBSCRIBE_BACK_BUTTON_COM).doClick();
					if (members) {
						status$ = "stopping script.";
						if (verbose) {
							log("You are not a member.");
						}
						stopScript(true);
					} else {
						sleep(random(500, 1000));
						worldHop(members, maxping, maxpop); // try again
					}
				}
				if (returnText.contains("must have a total")) {
					interfaces.getComponent(LOBBY_PARENT, BACK_BUTTON).doClick();
					sleep(random(500, 1000));
					worldHop(members, maxping, maxpop); // try again
				}
				if (returnText.contains("has not logged out")) {
					interfaces.getComponent(LOBBY_PARENT, BACK_BUTTON).doClick();
					sleep(random(10000, 15000));
					worldHop(members, maxping, maxpop); // try again
				}
				if (returnText.contains("standing in a members-only")) {
					interfaces.getComponent(LOBBY_PARENT, BACK_BUTTON).doClick();
					sleep(random(10000, 15000));
					if (members) {
						status$ = "stopping script.";
						if (verbose) {
							log("You are not a member.");
						}
						stopScript(true);
					} else {
						worldHop(members, maxping, maxpop); // try again
					}
				}
				if (returnText.contains("login limit exceeded")) {
					interfaces.getComponent(LOBBY_PARENT, BACK_BUTTON).doClick();
					sleep(random(1000, 1500));
					worldHop(members, maxping, maxpop); // try again
				}
			}
		}
	}

	private boolean writePrices() {
		try {
			final BufferedWriter out = new BufferedWriter(new FileWriter(PRICE_FILE));
			out.write(getDate() + ":" + wineID + ":" + winePrice + "\n");
			out.write(getDate() + ":" + lawID + ":" + lawPrice);
			out.close();
			;
		} catch (final Exception ignored) {
			return false;
		}
		return true;
	}
	
	/*
	 * ChangeLog:
	 * 
	 * v .99 First Public Release
	 * 
	 * v 1.00 Added run check/set
	 * 
	 * v 1.01 Added < 90 check to when to rest.
	 * 
	 * v 1.02 Added world hopping. Changed spell selection method to
	 * if(!spellSelected) to while(!spellSelected) Wrapped extra-loop grab in
	 * try/catch removed unneeded GTFO case, as it's better to use one case to
	 * Teleport.
	 * 
	 * v 1.03 Clean up some bugs in banking, and walking to temple afterward.
	 * Increased sleep delay after clicking rest. Removed compile from update,
	 * it doesn't work... updated worldHop from jtryba's post. It's still a
	 * little buggy, but working Well Enough™ for us.
	 * 
	 * v 1.04 Updated worldHop method form jtryba's post. Added a null check for
	 * the output of "ping" command in pingHost.
	 * 
	 * v 1.05 Added a timer in worldHop to bounce it out of a while loop. It got
	 * stuck on me once, not sure if this is the fix for it, but I'll throw it
	 * out and see.
	 * 
	 * v 1.10 Updated worldHop from jtryba's blog. Added a GUI
	 * 
	 * v 1.11 Re-added timer to kick out of possibly buggy worldHop loop.
	 * Changed GUI to allow control of #players in temple before hopping. Pings
	 * server before updating so script doesn't hang forever waiting on server
	 * if it's down.
	 * 
	 * v 1.12 Increased post-teleport delay to avoid 2x tele's. Increased delay
	 * after eating. Changed eat/get food routines.
	 * 
	 * v 1.13 Eliminated use of changing strings in paint, probable cause of
	 * memory issues in Windoze. Changed wait spot.
	 * 
	 * v 1.14 Changed from looking to see if we are in the restArea (near
	 * minstrel) to look to see if next destination is in restArea, ie: where
	 * the red flag in the minimap is. It's a smoother way to walk to the
	 * minstrel to rest. Imported mWine's wine-grab routine, with some mods to
	 * adapt to BWG. Releasing for testing to see if it'll do better grabbing.
	 * 
	 * v 1.15 Restored spell check in grab loop. Fixed problem with walking to
	 * bank with no law runes, ie: getting stuck at tele-landing spot. Updated
	 * method to determine if player is a member from account.isMemeber() to
	 * AccountManager.isMember(playerName)
	 * 
	 * v 1.16 Changed moving to the waitSpot to
	 * walking.walkTileOnScreen(waitSpot); Ensured that waitSpot will change 33%
	 * of the time after a miss. Found a problem when encountering a random. The
	 * grab loop will finish then switch (maybe?) to the random handler. added a
	 * check to see if we're standing on the waitSpot, this will hopefully cure
	 * the the random issue.
	 * 
	 * v 1.17 Fix for laws being withdrawn issue Updated worldHop. Changes to
	 * adapt to new RSBot security.
	 * 
	 * v 1.18 Change GlobalConfiguration to Configuration Added waitPlayerMoving
	 * to waitspot routine. Wrapped some bug in a try/catch..
	 * 
	 * v 1.19 Added landingArea to try to make bot walk to temple with wine in
	 * inventory is < max and starting script. FINALLY got log info from
	 * mageguy190. Added laws in bank != null check when checking to see if we
	 * got the laws, assumed that if it *IS* null, we got some laws, since it
	 * can't get there without having laws in the bank in the first place. The
	 * count will be updated first run thru the loop anyhow, so the inaccuracy
	 * isn't important. Randomized waitSpot at Startup. Added Kill button to
	 * paint. Fixed moving paint button when running resizable.
	 * 
	 * v 1.20 Stomped numberous bugs.
	 */

}

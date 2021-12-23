import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


public class MapUI implements ActionListener {

	JFrame frame;
	JPanel panel;
	JPanel panUI;
	JPanel panB;
	JPanel panR;

	JButton addB;
	JButton addR;
	JButton remB;
	JButton remR;
	JButton sPath;
	JButton pop;
	JButton mst;
	JLabel padding = new JLabel();

	Map map;

	HashMap<String, JLabel> roadMap;
	GridBagConstraints gbc;

	public static void main(String[] args) {
		MapUI ui = new MapUI();
		ui.init();
	}

	public void init() {
		map = new Map();
		roadMap = new HashMap<>();

		// make a JFrame
		frame = new JFrame();
		frame.setTitle("Map Builder.java");
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(500, 600);

		// make a JPanel
		panel = new JPanel();
		panel.setBackground(new Color(148,0,211));
		panel.setLayout(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		panUI = new JPanel();
		panUI.setBackground(Color.cyan);
		panUI.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

		panB = new JPanel();
		panB.setBackground(new Color(150, 150, 150));
		panB.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

		panR = new JPanel();
		panR.setBackground(new Color(200, 200, 200));
		panR.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

		panUI.setLayout(new GridLayout(0, 1));
		panB.setLayout(new GridLayout(0, 1));
		panR.setLayout(new GridLayout(0, 1));

		gbc = new GridBagConstraints();

		// make a JLabel
		Font tFont = new Font("Times New Roman", 35, 30);
		JLabel title = new JLabel();
		title.setText("Map Builder");
		title.setFont(tFont);
		title.setBorder(BorderFactory.createEmptyBorder(7, 5, 5, 5));
		title.setForeground(Color.blue);

		JLabel title2 = new JLabel();
		title2.setText("Map Finder");
		title2.setFont(tFont);
		title2.setForeground(Color.blue);

		tFont = new Font("Times New Roman", 25, 30);
		JLabel titleB = new JLabel();
		titleB.setText("Building List");
		titleB.setFont(tFont);
		titleB.setForeground(Color.blue);

		JLabel titleR = new JLabel();
		titleR.setText("Road List");
		titleR.setFont(tFont);
		titleR.setForeground(new Color(50, 75, 255));

		// make a JButton
		addB = new JButton("Add Building");
		addB.setBounds(250, 300, 50, 20);
		addB.addActionListener(this);
		addR = new JButton("Add Road");
		addR.addActionListener(this);
		remB = new JButton("Remove Building");
		remB.addActionListener(this);
		remR = new JButton("Remove Road");
		remR.addActionListener(this);

		sPath = new JButton("Find Shortest Path");
		sPath.addActionListener(this);

		mst = new JButton("Minimum Spanning Tree");
		mst.addActionListener(this);

		pop = new JButton("Auto Populate Graph");
		pop.addActionListener(this);

		// adding one thing to other
		frame.add(panel);
		panel.add(panUI, "West");
		panel.add(panB);
		panel.add(panR, "East");

		panUI.add(title);
		panUI.add(addB);
		panUI.add(addR);
		panUI.add(remB);
		panUI.add(remR);
		panUI.add(title2);
		panUI.add(sPath);
		panUI.add(mst);

		panB.add(titleB);
		panB.add(pop);
		panR.add(titleR);
		panR.add(padding);

		frame.pack();
	}

	private boolean addBuilding(boolean finished) {
		String bName = JOptionPane.showInputDialog(null, "Input Building Name");
		finished = map.addBuilding(bName);

		if (finished) {
			JLabel label = new JLabel(bName);
			panB.add(label);
			label.setFont(tFont);
			panB.updateUI();
			roadMap.put(bName, label);
			JOptionPane.showMessageDialog(null, "Added Building: " + bName);
		} else
			JOptionPane.showMessageDialog(null, "Failed to Add Building");
		return finished;
	}

	private boolean removeBuilding(boolean finished) { // add roads removed by building
		String bName = JOptionPane.showInputDialog(null, "Input Building Name");
		finished = map.removeBuilding(bName);

		if (finished) {
			for (String b : roadMap.keySet()) {
				if(b.contains(bName)) {
					removeRoad(b);
				}
				if(b.substring(b.length()/2,b.length()-1).equals(bName)) { //b.contains(bName
					removeRoad(b);
				}
			}
			panB.remove(roadMap.get(bName));
			panB.updateUI();
			roadMap.remove(bName);
			JOptionPane.showMessageDialog(null, "Removed Building: " + bName);
		} else
			JOptionPane.showMessageDialog(null, "Failed to Remove Building");
		return finished;
	}

	private boolean addRoad(boolean finished) {
		String rStart = JOptionPane.showInputDialog(null, "Input Starting Location");
		String rEnd = JOptionPane.showInputDialog(null, "Input Destination");
		String rLen = JOptionPane.showInputDialog(null, "Input length (integer)");
		try {
			int rLength = Integer.parseInt(rLen);
			finished = map.addRoad(rStart, rEnd, rLength);
		} catch (Exception except) {
			finished = false;
		}

		if (finished) {
			JLabel label = new JLabel("(" + rStart + ", " + rEnd + ", " + rLen + ")");
			panR.add(label);
			label.setFont(tFont);
			panR.updateUI();
			roadMap.put(rStart + rEnd, label);
			JOptionPane.showMessageDialog(null, "Added road from " + rStart + " to " + rEnd + " of length " + rLen);
		} else
			JOptionPane.showMessageDialog(null, "Failed to Add Raod");
		return finished;
	}

	private boolean removeRoad(boolean finished) {
		String rStart = JOptionPane.showInputDialog(null, "Input Starting Location");
		String rEnd = JOptionPane.showInputDialog(null, "Input Destination");
		finished = map.removeRoad(rStart, rEnd);

		if (finished) {
			panR.remove(roadMap.get(rStart + rEnd));
			panR.updateUI();
			roadMap.remove(rStart + rEnd);
			JOptionPane.showMessageDialog(null, "Removed road from " + rStart + " to " + rEnd);
		} else
			JOptionPane.showMessageDialog(null, "Failed to Remove Road");
		return finished;
	}

	private boolean shortestPath(boolean finished) {
		String rStart = JOptionPane.showInputDialog(null, "Input Starting Location");
		String rEnd = JOptionPane.showInputDialog(null, "Input Destination");

		int rLength = map.shortestLength(rStart, rEnd);
		List<String> pathLst = map.shortestPath(rStart, rEnd);
		String path = "";
		for (String s : pathLst)
			path += s + " ";
		finished = rLength > 0 ? true : false;
		finished = pathLst.size() > 0 ? true : false;
		if (finished) {
			int second = map.secondShortestPath(rStart, rEnd);
			if (second > 0)
				JOptionPane.showMessageDialog(null, "Shortest Path:  " + path + "\nShortest Path Length: " + rLength
						+ "\n2nd Shortest Path Length: " + second);
			else
				JOptionPane.showMessageDialog(null, "Shortest Path: " + path + "\nPath Length: " + rLength);
		} else
			JOptionPane.showMessageDialog(null, "No Valid Paths");
		return finished;
	}

	private boolean msTree(boolean finished) {
		int rLength = map.minimumTotalLength();
		finished = rLength > 0 ? true : false;
		if (finished)
			JOptionPane.showMessageDialog(null, "The Minimum Spanning Tree is of Length: " + rLength);
		else
			JOptionPane.showMessageDialog(null, "No Valid Trees");
		return finished;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		panB.remove(pop);
		panR.remove(padding);
		JButton buttonClicked = (JButton) e.getSource();

		Boolean finished = true;
		if (buttonClicked.equals(addB)) { // add building
			addBuilding(finished);

		} else if (buttonClicked.equals(addR)) { // add road
			addRoad(finished);

		} else if (buttonClicked.equals(remB)) { // remove building
			removeBuilding(finished);

		} else if (buttonClicked.equals(remR)) { // remove road
			removeRoad(finished);
		} else if (buttonClicked.equals(sPath)) { // find shortest path
			shortestPath(finished);
		} else if (buttonClicked.equals(mst)) { // find shortest path
			msTree(finished);
		} else if (buttonClicked.equals(pop)) { // find shortest path
			popDefault();
		} else {
			JOptionPane.showMessageDialog(null, "Unknown Button");
		}
		frame.pack();

	}

	Font tFont = new Font("Georgia", 35, 30);

	private void addBuilding(String bName) {
		map.addBuilding(bName);
		JLabel label = new JLabel(bName);
		panB.add(label);
		label.setFont(tFont);
		panB.updateUI();
		roadMap.put(bName, label);
	}

	private void addRoad(String rStart, String rEnd, int rLen) {
		map.addRoad(rStart, rEnd, rLen);
		JLabel label = new JLabel("(" + rStart + ", " + rEnd + ", " + rLen + ")");
		panR.add(label, gbc);
		label.setFont(tFont);
		panR.updateUI();
		roadMap.put(rStart + rEnd, label);
	}
	private void removeRoad(String road) {
			panR.remove(roadMap.get(road));
			panR.updateUI();
	}

	private void popDefault() {
		panB.remove(pop);
		panR.remove(padding);
		addBuilding("a");
		addBuilding("b");
		addBuilding("c");
		addBuilding("d");

		addRoad("a", "b", 1);
		addRoad("a", "c", 2);
		addRoad("b", "d", 3);
		addRoad("c", "d", 4);

		frame.pack();
	}
}

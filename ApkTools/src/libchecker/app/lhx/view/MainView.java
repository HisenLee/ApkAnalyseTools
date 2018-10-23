package libchecker.app.lhx.view;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import libchecker.app.lhx.action.CheckInput;
import libchecker.app.lhx.action.CheckLibExecution;
import libchecker.app.lhx.action.CheckLibExecution.IGetProcessCallBack;

public class MainView {
	
	private JFrame mMainFrame = null;
	private JPanel mMainPanel = null;
	
	private JLabel mApkPathLabel = null;
	private JTextField mApkPathField = null;
	private JButton mApkPathBtn = null;
	
	private JButton mStartBtn = null;
	
	private JScrollPane mScrollPane = null;
	private JTextArea mTextArea = null;
	
	private JFileChooser mFileChooser = null;
	private String apkPath;
	private CheckInput checkInput;
	private StringBuilder mTextAreaStr = new StringBuilder();
	
	public MainView() {
		initView();
		
		checkInput = new CheckInput();
	}
	
	/**
	 * initialize All components
	 */
	private void initView() {
		// init little Components
		initLittleComps();
		// init MainPane
		initMainPane();
		// init MainFrame
		initMainFrame();
	}
	
	/**
	 * initialize LittleComp
	 */
	private void initLittleComps() {
		mApkPathLabel = new JLabel("APK Path: ");
		mApkPathLabel.setBounds(10,20,80,25);
		
		mApkPathField = new JTextField(20);
		mApkPathField.setBounds(90,20,400,25);
		
		mApkPathBtn = new JButton("Choose...");
		mApkPathBtn.setBounds(495,20, 90, 25);
		mApkPathBtn.addActionListener(IClickListener);
		
		mStartBtn = new JButton("Startanalyze");
		mStartBtn.setBounds(10,80,120,25);
		mStartBtn.addActionListener(IClickListener);
		
		mTextArea = new JTextArea();
		mTextArea.setEditable(false);
		mTextArea.setLineWrap(true);  
		mTextArea.setWrapStyleWord(true); 
		mTextArea.setFont(new Font("±κΏ¬Με", Font.BOLD, 16));
		mScrollPane = new JScrollPane(mTextArea);
		mScrollPane.setBounds(10,170, 550, 330);
	}
	/**
	 * initialize MainPane
	 */
	private void initMainPane() {
		mMainPanel = new JPanel();
		mMainPanel.setLayout(null);
		mMainPanel.add(mApkPathLabel);
		mMainPanel.add(mApkPathField);
		mMainPanel.add(mApkPathBtn);
		mMainPanel.add(mStartBtn);
		mMainPanel.add(mScrollPane);
	}
	/**
	 * initialize MainFrame
	 */
	private void initMainFrame() {
		mMainFrame = new JFrame();
		mMainFrame.setTitle("ApkLibCheck");
		mMainFrame.setLocation(300,200);
        mMainFrame.setSize(600, 550);
		mMainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mMainFrame.setResizable(false);
		
		mMainFrame.add(mMainPanel);
	}
	
	/**
	 * Run Frame
	 */
	public void LaunchMainFrame() {
		mMainFrame.setVisible(true);
	}
	
	/**
	 * SetApkPath
	 * @param file
	 */
	private void setApkPath(File file) {
		if (file != null) {
			if (file.isDirectory()) {
				this.apkPath = file.getAbsolutePath();
			} else if (file.isFile()) {
				this.apkPath = file.getAbsolutePath();
			}
		} else {
			System.out.println("error ...");
		}
	}

	public String getApkPath() {
		return this.apkPath;
	}
	
	/**
	 * ChooseApkDir
	 * @return
	 */
	private JFileChooser getJFileChooser() {
		if (mFileChooser == null) {
			mFileChooser = new JFileChooser();
			mFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES );
//			mFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY );
			mFileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
		}

		int result = mFileChooser.showOpenDialog(mMainFrame);
		if (JFileChooser.CANCEL_OPTION == result) {
//			System.out.println(" FileChooser.CANCEL_OPTION..." + result);
		} else if (JFileChooser.APPROVE_OPTION == result) {
//			System.out.println(" JFileChooser.APPROVE_OPTION.." + result);
			setApkPath(mFileChooser.getSelectedFile());
			mApkPathField.setText(getApkPath());
		} else if (JFileChooser.ERROR_OPTION == result) {
//			System.out.println(" JFileChooser.ERROR_OPTION..." + result);
		}
		return mFileChooser;
	}
	
	/**
	 * BtnClickListener
	 */
	ActionListener IClickListener = new ActionListener() {
		
		public void actionPerformed(ActionEvent event) {
			if(event.getSource().equals(mApkPathBtn)) {
				// ChooseApkDir
				getJFileChooser();
				// CheckDir
				checkInput.checkDir(getApkPath());
				
			} else if(event.getSource().equals(mStartBtn)) {
				
				if(checkInput.checkDir(getApkPath())) {
					final long start = System.currentTimeMillis();
					
					mTextAreaStr.append("Execution started......").append("\n");
					mTextArea.setText(mTextAreaStr.toString());
					mStartBtn.setEnabled(false);
				
					// The time-consuming operation requires the addition of the child thread, 
					// otherwise the TextArea will not be refreshed in time 
					// and will lead to the main thread UI freeze
					new Thread(new Runnable() {
						
						public void run() {
							CheckLibExecution libExecution = new CheckLibExecution(getApkPath(), getProcessCallBack);
							libExecution.generateExcelResult();
							
							mTextAreaStr.append("Execution finished! Totally " + 
									(System.currentTimeMillis() - start) / 1000
									+ " seconds.").append("\n");
							mTextArea.setText(mTextAreaStr.toString());
							mStartBtn.setEnabled(true);
						}
					}).start();
					
					
					
					
				}
			
			}
			
		}
	};
	
	
	IGetProcessCallBack getProcessCallBack = new IGetProcessCallBack() {
		
		public void getProcess(String current) {
			mTextAreaStr.append(current).append("\n");
			mTextArea.setText(mTextAreaStr.toString());
//			mTextArea.paintImmediately(mTextArea.getBounds());
		}
	};
	
	

}

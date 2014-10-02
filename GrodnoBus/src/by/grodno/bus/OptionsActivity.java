package by.grodno.bus;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.sun.mail.pop3.POP3SSLStore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.mail.*;

public class OptionsActivity extends SherlockPreferenceActivity { // Activity {
	private Dialog progress;
	private ProgressBar progressbar;
	private TextView statusTextView;
	private Context context;
	private String dbDate;

	private final int c_init=-3;
	private final int c_max=-1;
	private final int c_progress=-2;
	private final int c_done=1;
	private final int c_updateerror=-4;
	private final int c_updatednone=0;
	private String err_msg="";

	public class UpdateThread extends Thread {
		String POP_AUTH_USER = "green2005update@gmail.com";
		String POP_AUTH_PWD = "androidupdate1";
		String FOLDER_INDOX = "INBOX"; 
		String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
		private Handler mainHandler;
		Properties pop3Props = null;
		Session session = null;
		Store store = null;

		UpdateThread(Handler mainHandler) {
			this.mainHandler = mainHandler;
		}

		public String getDBfileName() {
			File f = context.getDatabasePath(PDBDataSource.dbName);
			f.getAbsolutePath();
			return f.getAbsolutePath();
		}

		private Folder getGmailInboxFolder() {
			try {
				pop3Props = new Properties();
				session = Session.getInstance(pop3Props, null);
				store = session.getStore("imaps");// ("pop3s");
				// store.connect("pop.gmail.com", POP_AUTH_USER,POP_AUTH_PWD);
				store.connect("imap.gmail.com", POP_AUTH_USER, POP_AUTH_PWD);

				Folder folder = store.getFolder(FOLDER_INDOX);
				folder.open(Folder.READ_ONLY);
				return folder;
			} catch (Exception e) {
			///	err_msg=e.getMessage();
				return null;

			}
		}

		private Folder getYandexInboxFolder() {
			try {
				String host = "pop.yandex.ru";
				String user = "green2005update";
				String password = "androidupdate1";
				
				pop3Props=new Properties();
				 
				pop3Props.setProperty("mail.pop3.socketFactory.class", SSL_FACTORY);
				 pop3Props.setProperty("mail.pop3.socketFactory.fallback", "false");
				 pop3Props.setProperty("mail.pop3.port",  "995");
				 pop3Props.setProperty("mail.pop3.socketFactory.port", "995");
				    
				// connect to my pop3 inbox
				
				 /*
				 pop3Props = System.getProperties();
				session = Session.getDefaultInstance(pop3Props);
				store = session.getStore("pop3");
				*/
				URLName url = new URLName("pop3", host, 995, "",
			            user, password);

			    session = Session.getInstance(pop3Props, null);
			    store = new POP3SSLStore(session, url);
			    
				store.connect(host, user, password);
				
				
				Folder inbox = store.getFolder("Inbox");
				inbox.open(Folder.READ_ONLY);
				return inbox; 
			} catch (Exception e) {
				err_msg=e.getMessage();
				Log.d("error", e.getMessage());
				return null;
			}
		}

		private void updateSchedule() {
			boolean updated = false;
			try {
				// Properties
				boolean isYandexMail = false;
				pop3Props = null;
				session = null;
				store = null;
				Folder folder = getYandexInboxFolder();
				if (folder == null) {
					folder = getGmailInboxFolder();
				} else {
					isYandexMail = true;
				}

				if (folder == null) {
					android.os.Message msg = new android.os.Message();
					msg.arg1 = c_updateerror;
					msg.arg2 = 0;
					mainHandler.sendMessage(msg);
					return;
				}

				// Message[] messages =
				// folder.search(new FlagTerm(new Flags(Flags.Flag.FLAGGED),
				// false));
				javax.mail.Message[] messages = folder.getMessages();//
				if (messages.length > 0) {
					for (javax.mail.Message m : messages) {
						// javax.mail.Message m=messages[0];
						String s = m.getSubject();
						//if (s.length() != 13)
						//	continue;
						String s1 = s.substring(0, 3);
						if (!s1.equalsIgnoreCase("zip"))
							continue;
						s=s.replace("zip", "");
						String ds = s.substring(0, 2);
						if ((ds.compareTo("31") == 1)
								|| (ds.compareTo("00") == -1))
							continue;
						String ms = s.substring(3, 5);
						if ((ms.compareTo("12") == 1)
								|| (ms.compareTo("00") == -1))
							continue;
						String ys = s.substring(6, 10);
						if ((ys.compareTo("2100") == 1)
								|| (ys.compareTo("2000") == -1))
							continue;

						String dds = dbDate.substring(0, 2);
						String dms = dbDate.substring(3, 5);
						String dys = dbDate.substring(6, 10);

						int i = (ys.compareTo(dys));
						if (i == 0) {
							i = (ms.compareTo(dms));
							if (i == 0)
								i = (ds.compareTo(dds));
						}

						if (i <= 0) {
							/*
							 * android.os.Message msg = new
							 * android.os.Message(); msg.arg1 = 0; msg.arg2 = 0;
							 * mainHandler.sendMessage(msg); return;
							 */
							continue;
						}
						;
						
						android.os.Message initmsg = new android.os.Message();
						initmsg.arg1 = c_init;
						mainHandler.sendMessage(initmsg);
						String newFileName = getDBfileName()+".tmp";
						String dbFileName = getDBfileName();
						OutputStream stream = new FileOutputStream(newFileName);
						Multipart multipart = (Multipart) m.getContent();

						if (isYandexMail) {
							int read = 0;
							for (int j = 0; j < multipart.getCount(); j++) {
								BodyPart bp = multipart.getBodyPart(j);
								ZipInputStream is = new ZipInputStream(bp.getInputStream());
								if (j==0){
									android.os.Message msg = new android.os.Message();
									msg.arg1 = c_max;
									msg.arg2 = bp.getSize();
									mainHandler.sendMessage(msg);
								}
								ZipEntry ze= is.getNextEntry();
								byte[] buffer = new byte[2048];
								int length = 0;

								read=2048;
								android.os.Message msg2 = new android.os.Message();
								msg2.arg1 = c_progress;
								msg2.arg2 = read;
								mainHandler.sendMessage(msg2);

								while ((length = is.read(buffer)) > 0) {
									stream.write(buffer, 0, length);

									read += length;
									msg2 = new android.os.Message();
									msg2.arg1 = c_progress;
									msg2.arg2 = read;
									mainHandler.sendMessage(msg2);

								}
								is.closeEntry();
								android.os.Message msg = new android.os.Message();
								msg.arg1 = c_progress;
								msg.arg2 = bp.getSize();
								mainHandler.sendMessage(msg);
								
							}
						} else

						{
							int read = 0;
							if (multipart.getCount() > 1) {
								BodyPart bodyPart = multipart.getBodyPart(1);
								InputStream stream1 = bodyPart.getInputStream();
								byte[] buffer = new byte[2048];
								int length = 0;
								
								android.os.Message msg = new android.os.Message();
								msg.arg1 = c_max;
								msg.arg2 = bodyPart.getSize();
								mainHandler.sendMessage(msg);
								
								read=2048;
								android.os.Message msg2 = new android.os.Message();
								msg2.arg1 = c_progress;
								msg2.arg2 = read;
								mainHandler.sendMessage(msg2);
								
								
								while ((length = stream1.read(buffer)) > 0) {
									stream.write(buffer, 0, length);

									read += length;
									msg2 = new android.os.Message();
									msg2.arg1 = c_progress;
									msg2.arg2 = read;
									mainHandler.sendMessage(msg2);
								}
								
								android.os.Message msg3 = new android.os.Message();
								msg3.arg1 = c_progress;
								msg3.arg2 = bodyPart.getSize();
								mainHandler.sendMessage(msg3);
							}
						}
						stream.flush();
						stream.close();
						if (checkIsDbCorrect(newFileName)){
							File sdCard=Environment.getExternalStorageDirectory();
							File tmpfile=new File(newFileName);
							File dbFile=new File(dbFileName);
							tmpfile.renameTo(dbFile);
						}else
						{
							try{
							File tmpfile=new File(newFileName);
							tmpfile.delete();
							}catch(Exception e){e.printStackTrace();}
							
							android.os.Message msg = new android.os.Message();
							msg.arg1 = c_updateerror;
							err_msg="Ошибка обновления";
							msg.arg2 = 1;
							mainHandler.sendMessage(msg);
							return;
						}
						
						dbDate = m.getSubject().replace("zip", "");
						updated = true;
						break;
					}
				}
				
				
				if (folder != null)
					folder.close(false);
				if (store != null)
					store.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			android.os.Message msg = new android.os.Message();
			if (updated) {
				msg.arg1 = c_done;
				msg.arg2 = 1;
			} else {
				msg.arg1 = c_updatednone;
				msg.arg2 = 0;
			}
			mainHandler.sendMessage(msg);
		}

		public void run() {
			updateSchedule();
		}
	}
	
	private boolean checkIsDbCorrect(String fileName){
		try{
			SQLiteDatabase db = SQLiteDatabase.openDatabase(fileName, null,
				SQLiteDatabase.NO_LOCALIZED_COLLATORS
						| SQLiteDatabase.CREATE_IF_NECESSARY);
		
			String sql = " select name from buses group by name order by length(name),name";
			Cursor cr=db.rawQuery(sql, null);
			if (cr.getCount()==0){
				cr.close();
				db.close();
				return false;	
			}
			cr.close();
			db.close();
			return true;
		} catch (Exception e){
			e.printStackTrace();
			return false;
		}
		
	}

	private void fillDBDate() {
		Resources res = getResources();
		dbDate = res.getString(R.string.scheduleDate);
		SharedPreferences pref = getSharedPreferences("by.grodno.bus",
				Activity.MODE_PRIVATE);
		dbDate = pref.getString("scheduleDate", dbDate);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(GrodnoBusActivity.THEME);
		super.onCreate(savedInstanceState);
		setTitle("Расписание автобусов");
		context = this;
		fillDBDate();
		addPreferencesFromResource(R.xml.preferences);
		Preference prefEmail = findPreference("sendEmail");
		prefEmail.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				// TODO Auto-generated method stub
				final Intent emailIntent = new Intent(
						android.content.Intent.ACTION_SEND);
				emailIntent.setType("plain/text");
				emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
						new String[] { "green_2005@tut.by" });
				emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
						"GrodnoBusSchedule App review");
				emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
				startActivity(Intent.createChooser(emailIntent, "Send mail..."));

				return true;
			}
		});

		Preference pref = findPreference("updateSchedule");
		pref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				// Toast.makeText(context, "click", Toast.LENGTH_LONG).show();
				Handler mainhandler = new Handler() {
					public void handleMessage(Message msg) {
						if (msg.arg1==c_updateerror){
							if (err_msg.contains("POP3")){
								err_msg="Connect failed";
								
							}
							Toast.makeText(context, OptionsActivity.this.err_msg,
									Toast.LENGTH_LONG).show();
							if (progress != null) {
								progress.dismiss();
							}
						}
						
						if (msg.arg1==c_init){
							statusTextView.setText("Инициализация");
						}else
						if (msg.arg1 == c_max) {
							if (progressbar != null) {
								progressbar.setMax(msg.arg2);
								progressbar.setVisibility(View.VISIBLE);
								statusTextView.setText("Получение обновлений");
							}
						} else if (msg.arg1 == c_progress) {
							if (progressbar != null) {
								progressbar.setProgress(msg.arg2);
							}
						} else if (msg.arg1 == c_updatednone) {
							Toast.makeText(context, "Обновление не требуется",
									Toast.LENGTH_LONG).show();
							if (progress != null) {
								progress.dismiss();
							}
						} else if (msg.arg1 == c_done) {
							Toast.makeText(context, "Обновление завершено",
									Toast.LENGTH_LONG).show();
							SharedPreferences pref = getSharedPreferences(
									"by.grodno.bus", Activity.MODE_PRIVATE);
							Editor settings = pref.edit();
							settings.putString("scheduleDate", dbDate);
							settings.commit();

							if (progress != null) {
								progress.dismiss();
							}
							// dbDate=pref.getString("scheduleDate", dbDate);

						}
					};
				};
				UpdateThread th = new UpdateThread(mainhandler);
				progress = new Dialog(context);
				progress.setContentView(R.layout.progresslayout);
				progress.setTitle("Обновление");
				progressbar = (ProgressBar) progress
						.findViewById(R.id.progressBar1);
				statusTextView=(TextView)progress.findViewById(R.id.statusTextView);
				progress.show();
				// /progress = ProgressDialog.show(context, "",
				// "Обновление ...");
				th.start();
				return true;
			}
		});

		ListPreference stylePref = (ListPreference) findPreference("style");
		stylePref
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						// TODO Auto-generated method stub
						// Toast.makeText(context, newValue.toString(),
						// Toast.LENGTH_LONG).show();
						String s = newValue.toString();
						if (s.equals("0")
								|| (s.equals("R.style.Theme_Sherlock"))) {
							GrodnoBusActivity.THEME = R.style.Theme_Sherlock;
						} else if (s.equals("1")
								|| (s.equals("R.style.Theme_Sherlock_Light"))) {
							GrodnoBusActivity.THEME = R.style.Theme_Sherlock_Light;
						} else if (s.equals("2")
								|| s.equals("R.style.Theme_Sherlock_Light_DarkActionBar")) {
							GrodnoBusActivity.THEME = R.style.Theme_Sherlock_Light_DarkActionBar;
						}
						setTheme(GrodnoBusActivity.THEME);
						SharedPreferences pref = getSharedPreferences(
								"by.grodno.bus", Activity.MODE_PRIVATE);
						Editor edit = pref.edit();
						edit.putString("style", s);
						edit.commit();
						Intent refresh = new Intent(context,
								OptionsActivity.class);
						startActivity(refresh);
						OptionsActivity.this.finish();

						return true;
					}
				});

		/*
		 * super.onCreate(savedInstanceState); context=this;
		 * setContentView(R.layout.options); ImageButton checkMailBtn =
		 * (ImageButton) findViewById(R.id.btnRefreshSchedule);
		 * checkMailBtn.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { // TODO Auto-generated method
		 * stub Handler mainhandler = new Handler() { public void
		 * handleMessage(Message msg) { if (progress!=null)
		 * {progress.dismiss();} Toast.makeText(context, "Обновление завершено",
		 * Toast.LENGTH_LONG).show(); }; }; UpdateThread th=new
		 * UpdateThread(mainhandler); progress=ProgressDialog.show(context, "",
		 * "Обновление ..."); th.start(); } });
		 */
	}

}

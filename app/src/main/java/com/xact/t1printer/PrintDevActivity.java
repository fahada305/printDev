package com.xact.t1printer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.topwise.cloudpos.aidl.AidlDeviceService;
import com.topwise.cloudpos.aidl.printer.AidlPrinter;
import com.topwise.cloudpos.aidl.printer.AidlPrinterListener;
import com.topwise.cloudpos.aidl.printer.PrintItemObj;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/**
 * 打印机测试工具类
 * @author Tianxiaobo
 *
 */
/**
 * 打印机测试工具类
 * @author Tianxiaobo
 *
 */
public class PrintDevActivity extends Activity implements View.OnClickListener {
	private static final String TAG = "TPW-BaseTestActivity";

	public static final String TOPWISE_SERVICE_ACTION = "topwise_cloudpos_device_service";

	private int showLineNum = 0;
	private long oldTime = -1;
	public static final long DELAY_TIME = 200;

	private ServiceConnection conn = new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName name, IBinder serviceBinder) {
			Log.d(TAG,"aidlService服务连接成功");
			if(serviceBinder != null){	//绑定成功
				AidlDeviceService serviceManager = AidlDeviceService.Stub.asInterface(serviceBinder);
				onDeviceConnected(serviceManager);
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.d(TAG,"AidlService服务断开了");
		}
	};

		private AidlPrinter printerDev = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.setContentView(R.layout.printdev);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.unbindService(conn);
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		bindService();
	}

	//绑定服务
	public void bindService(){
		Intent intent = new Intent();
		intent.setAction(TOPWISE_SERVICE_ACTION);
		final Intent eintent = new Intent(createExplicitFromImplicitIntent(this,intent));
		boolean flag = bindService(eintent, conn, Context.BIND_AUTO_CREATE);
		if(flag){
			Log.d(TAG,"服务绑定成功");
		}else{
			Log.d(TAG,"服务绑定失败");
		}
	}



	public static Intent createExplicitFromImplicitIntent(Context context, Intent implicitIntent) {
		// Retrieve all services that can match the given intent
		PackageManager pm = context.getPackageManager();
		List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);

		// Make sure only one match was found
		if (resolveInfo == null || resolveInfo.size() != 1) {
			return null;
		}

		// Get component info and create ComponentName
		ResolveInfo serviceInfo = resolveInfo.get(0);
		String packageName = serviceInfo.serviceInfo.packageName;
		String className = serviceInfo.serviceInfo.name;
		ComponentName component = new ComponentName(packageName, className);

		// Create a new intent. Use the old one for extras and such reuse
		Intent explicitIntent = new Intent(implicitIntent);

		// Set the component to be explicit
		explicitIntent.setComponent(component);

		return explicitIntent;
	}



	public void onDeviceConnected(AidlDeviceService serviceManager) {
		try {
			printerDev = AidlPrinter.Stub.asInterface(serviceManager.getPrinter());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public void printQrCode(View v){
		Bitmap qrcodeBitmap = QRCodeUtil.createQRImage("123456789",300,300,null);
		try{
			this.printerDev.printBmp(100, qrcodeBitmap.getWidth(), qrcodeBitmap.getHeight(), qrcodeBitmap, new AidlPrinterListener.Stub() {
				@Override
				public void onPrintFinish() throws RemoteException {
					showMessage("打二维码成功");
				}

				@Override
				public void onError(int arg0) throws RemoteException {
					showMessage("打二维码失败，错误码" + arg0 );
				}
			});
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void getPrintState(View v){
		try {
			int printState = printerDev.getPrinterState();
			showMessage(getResources().getString(R.string.print_status) + printState);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 打印文本
	 * @param v
	 * @createtor：Administrator
	 * @date:2015-8-4 下午2:19:28
	 */
	public void printText(View v){
		try {
			String startTime = getCurTime();
			showMessage(getResources().getString(R.string.print_start_time)+startTime);
  			printerDev.printText(new ArrayList<PrintItemObj>(){
				{
					add (new PrintItemObj ("sudhir kumar"));
					add (new PrintItemObj ("Mobile number: -9350503212"));
					add (new PrintItemObj ("default print data test"));
					add (new PrintItemObj ("Print data font enlargement", 24));
					add (new PrintItemObj ("Print data font enlargement", 24));
					add (new PrintItemObj ("Print data font enlargement", 24));
					add (new PrintItemObj ("Print data bold", 8, true));
					add (new PrintItemObj ("Print data bold", 8, true));
					add (new PrintItemObj ("Print data bold", 8, true));
					add (new PrintItemObj ("Left alignment test of print data", 8, false, PrintItemObj.ALIGN.LEFT));
					add (new PrintItemObj ("Left alignment test of print data", 8, false, PrintItemObj.ALIGN.LEFT));
					add (new PrintItemObj ("Left alignment test of print data", 8, false, PrintItemObj.ALIGN.LEFT));
					add (new PrintItemObj ("Print data center alignment test", 8, false, PrintItemObj.ALIGN.CENTER));
					add (new PrintItemObj ("Print data center alignment test", 8, false, PrintItemObj.ALIGN.CENTER));
					add (new PrintItemObj ("Print data center alignment test", 8, false, PrintItemObj.ALIGN.CENTER));
					add (new PrintItemObj ("Print data right alignment test", 8, false, PrintItemObj.ALIGN.RIGHT));
					add (new PrintItemObj ("Print data right alignment test", 8, false, PrintItemObj.ALIGN.RIGHT));
					add (new PrintItemObj ("Print data right alignment test", 8, false, PrintItemObj.ALIGN.RIGHT));
					add (new PrintItemObj ("Print data underline", 8, false, PrintItemObj.ALIGN.LEFT, true));
					add (new PrintItemObj ("Print data underline", 8, false, PrintItemObj.ALIGN.LEFT, true));
					add (new PrintItemObj ("Print data underline", 8, false, PrintItemObj.ALIGN.LEFT, true));
					add (new PrintItemObj ("Print data does not wrap test print data does not wrap test print data does not wrap test test", 8, false, PrintItemObj.ALIGN.LEFT, false, true));
					add (new PrintItemObj ("Print data does not wrap test print data does not wrap test print data does not wrap test print data does not wrap test", 8, false, PrintItemObj.ALIGN.LEFT, false, false));
					add (new PrintItemObj ("Print data does not wrap test", 8, false, PrintItemObj.ALIGN.LEFT, false, false));
					add (new PrintItemObj ("Print data line spacing test", 8, false, PrintItemObj.ALIGN.LEFT, false, true, 40));
					add (new PrintItemObj ("Print data line spacing test", 8, false, PrintItemObj.ALIGN.LEFT, false, true, 83));
					add (new PrintItemObj ("Print data line spacing test", 8, false, PrintItemObj.ALIGN.LEFT, false, true, 40));
					add (new PrintItemObj ("Print data character spacing test", 8, false, PrintItemObj.ALIGN.LEFT, false, true, 29,25));
					add (new PrintItemObj ("Print data character spacing test", 8, false, PrintItemObj.ALIGN.LEFT, false, true, 29,25));
					add (new PrintItemObj ("Print data character spacing test", 8, false, PrintItemObj.ALIGN.LEFT, false, true, 29,25));
					add (new PrintItemObj ("Left margin test of print data", 8, false, PrintItemObj.ALIGN.LEFT, false, true, 29,0,40));
					add (new PrintItemObj ("Left margin test of print data", 8, false, PrintItemObj.ALIGN.LEFT, false, true, 29,0,40));
					add (new PrintItemObj ("Left margin test of print data", 8, false, PrintItemObj.ALIGN.LEFT, false, true, 29,0,40));add(new PrintItemObj("\n\n\n"));
				}
			}, new AidlPrinterListener.Stub() {
				
				@Override
				public void onPrintFinish() throws RemoteException {
					String endTime = getCurTime();
					showMessage(getResources().getString(R.string.print_end_time)+endTime);
				}
				
				@Override
				public void onError(int arg0) throws RemoteException {
					showMessage(getResources().getString(R.string.print_error_code) + arg0);
				}
			});
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 打印位图
	 * @param v
	 * @createtor：Administrator
	 * @date:2015-8-4 下午2:39:33
	 */
	public void printBitmap(View v){
		try {
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher);
			String startTime = getCurTime();
			showMessage(getResources().getString(R.string.print_start_time)+startTime);
			this.printerDev.printBmp(100, bitmap.getWidth(), bitmap.getHeight(), bitmap, new AidlPrinterListener.Stub() {
				
				@Override
				public void onPrintFinish() throws RemoteException {
					String endTime = getCurTime();
					showMessage(getResources().getString(R.string.print_end_time)+endTime);
				}
				
				@Override
				public void onError(int arg0) throws RemoteException {
					showMessage(getResources().getString(R.string.print_error_code) + arg0 );
				}
			});

			this.printerDev.printText(new ArrayList<PrintItemObj>() {
				{
					add(new PrintItemObj("\n\n\n"));
				}
			},new PrintStateChangeListener());

		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {


	}

	private class PrintStateChangeListener extends AidlPrinterListener.Stub{

		@Override
		public void onError(int arg0) throws RemoteException {
			showMessage(getResources().getString(R.string.print_error_code) + arg0);
		}

		@Override
		public void onPrintFinish() throws RemoteException {
			String endTime = getCurTime();
			showMessage(getResources().getString(R.string.print_end_time)+endTime);
		}
		
	}

	private class PrintStateChangeListener1 extends AidlPrinterListener.Stub{

		@Override
		public void onError(int arg0) throws RemoteException {
			showMessage(getResources().getString(R.string.print_error_code) + arg0);
		}

		@Override
		public void onPrintFinish() throws RemoteException {
			String endTime = getCurTime();
			showMessage(getResources().getString(R.string.print_end_time)+endTime);
			printerDev.printText(new ArrayList<PrintItemObj>() {
				{
					add(new PrintItemObj("\n\n\n"));
				}
			},mListen);
		}

	}
	

	public void printBarCode(View v){
		try {
			String startTime = getCurTime();
			showMessage(getResources().getString(R.string.print_start_time)+startTime);
			this.printerDev.printBarCode(-1, 162, 18, 65, "23418753401", new PrintStateChangeListener());
			this.printerDev.printBarCode(-1, 162, 18, 66, "03400471", new PrintStateChangeListener());
			this.printerDev.printBarCode(-1, 162, 18, 67, "2341875340111", new PrintStateChangeListener());
			this.printerDev.printBarCode(-1, 162, 18, 68, "23411875", new PrintStateChangeListener());
			this.printerDev.printBarCode(-1, 162, 18, 69, "*23418*", new PrintStateChangeListener());
			this.printerDev.printBarCode(-1, 162, 18, 70, "234187534011", new PrintStateChangeListener());
			this.printerDev.printBarCode(-1, 162, 18, 71, "23418", new PrintStateChangeListener());
			this.printerDev.printBarCode(-1, 162, 18, 72, "23418", new PrintStateChangeListener());
			this.printerDev.printBarCode(-1, 162, 18, 73, "{A23418", new PrintStateChangeListener1());

		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setPrintHGray(View v){
		List<PrintItemObj> list =new ArrayList<>();
		list.add (new PrintItemObj ("The print density is set to 4"));
		list.add (new PrintItemObj ("The print density is set to 4"));
		list.add (new PrintItemObj ("The print density is set to 4"));
		list.add (new PrintItemObj ("The print density is set to 4"));
		list.add (new PrintItemObj ("The print density is set to 4"));
		try {
			printerDev.setPrinterGray(4);
			printerDev.printText(list, new AidlPrinterListener.Stub() {
				@Override
				public void onError(int i) throws RemoteException {
					showMessage("数据打印失败,错误码" + i);
				}

				@Override
				public void onPrintFinish() throws RemoteException {
					showMessage("数据打印成功");
				}
			});
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void setPrintLGray(View v){
		List<PrintItemObj> list1 =new ArrayList<>();
		list1.add (new PrintItemObj ("The print density is set to 1"));
		list1.add (new PrintItemObj ("The print density is set to 1"));
		list1.add (new PrintItemObj ("The print density is set to 1"));
		list1.add (new PrintItemObj ("The print density is set to 1"));
		list1.add (new PrintItemObj ("The print density is set to 1"));
		try {
			printerDev.setPrinterGray(1);
			printerDev.printText(list1, new AidlPrinterListener.Stub() {
				@Override
				public void onError(int i) throws RemoteException {
					showMessage("数据打印失败,错误码" + i);
				}

				@Override
				public void onPrintFinish() throws RemoteException {
					showMessage("数据打印成功");
				}
			});
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void setPrintNGray(View v){
		List<PrintItemObj> list1 =new ArrayList<>();
		list1.add(new PrintItemObj("打印浓度设置为2"));
		list1.add(new PrintItemObj("打印浓度设置为2"));
		list1.add(new PrintItemObj("打印浓度设置为2"));
		list1.add(new PrintItemObj("打印浓度设置为2"));
		list1.add(new PrintItemObj("打印浓度设置为2"));
		try {
			printerDev.setPrinterGray(2);
			printerDev.printText(list1, new AidlPrinterListener.Stub() {
				@Override
				public void onError(int i) throws RemoteException {
					showMessage("数据打印失败,错误码" + i);
				}

				@Override
				public void onPrintFinish() throws RemoteException {
					showMessage("数据打印成功");
				}
			});
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void printBigBuy(View v){
		try {
			String startTime = getCurTime();
			showMessage(getResources().getString(R.string.print_start_time)+startTime);
			printerDev.printText(new ArrayList<PrintItemObj>() {
                {
                    add(new PrintItemObj("POS purchase order", 16, true, PrintItemObj.ALIGN.CENTER));
                    add(new PrintItemObj("Merchant number: 00000000000 Terminal number: 100000000", 8, true, PrintItemObj.ALIGN.LEFT));
                    add(new PrintItemObj("Operator: 01 Acquirer: Lacala", 8, true, PrintItemObj.ALIGN.LEFT));
                    add(new PrintItemObj("Card number: 6214444 ****** 0095 1", 8, true, PrintItemObj.ALIGN.LEFT));
                    add(new PrintItemObj("Card organization: Domestic Credit Card", 8, true, PrintItemObj.ALIGN.LEFT));
                    add(new PrintItemObj("Card type: ICBC Validity: 20/12", 8, true, PrintItemObj.ALIGN.LEFT));
                    add(new PrintItemObj("Transaction category: Consumption", 16, true, PrintItemObj.ALIGN.LEFT));
                    add(new PrintItemObj("batch number:000001", 8, true, PrintItemObj.ALIGN.LEFT));
                    add(new PrintItemObj("Voucher number: 000033 Authorization number: 000000", 8, true, PrintItemObj.ALIGN.LEFT));
                    add(new PrintItemObj("Reference number: 1009000000033", 8, true, PrintItemObj.ALIGN.LEFT));
                    add(new PrintItemObj("Date / Time: 2017/10/10 11:11:11", 8, true, PrintItemObj.ALIGN.LEFT));
                    add(new PrintItemObj("Paid amount: RMB 100", 16, true, PrintItemObj.ALIGN.LEFT));
                    add(new PrintItemObj("Order amount: RMB 100", 8, true, PrintItemObj.ALIGN.LEFT));
                    add(new PrintItemObj("Promotion amount: RMB 0.00", 8, true, PrintItemObj.ALIGN.LEFT));
                    add(new PrintItemObj("Remarks:", 8, true, PrintItemObj.ALIGN.LEFT));
                    add(new PrintItemObj("AID:A000000333010101 TVR:008004600:", 8, true, PrintItemObj.ALIGN.LEFT));
                    add(new PrintItemObj("ARQC:ABCDEFDGJHHHGA ATC:0020:", 8, true, PrintItemObj.ALIGN.LEFT));
                    add(new PrintItemObj("Convenient payment and convenient life:", 8, true, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("Cardholder's signature:", 8, true, PrintItemObj.ALIGN.LEFT));
                    add(new PrintItemObj("I confirm the above transaction and agree to credit it to my card account", 8, true, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("I ACKNOWLEDGE SATISFACTORY RECEIPT OF RELATIVE GOODS/SERVICES", 8, true, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("--------------------------------", 8, true, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("SD_V2.1.8.11404.0 Customer Hotline:95016", 8, true, PrintItemObj.ALIGN.LEFT));
					add(new PrintItemObj("------------Merchant stubs------------", 8, true, PrintItemObj.ALIGN.LEFT));

					add(new PrintItemObj("\n\n\n"));
                }
            }, new AidlPrinterListener.Stub() {

                @Override
                public void onPrintFinish() throws RemoteException {
					String endTime = getCurTime();
                    showMessage(getResources().getString(R.string.print_end_time)+endTime);
                }

                @Override
                public void onError(int arg0) throws RemoteException {
                    showMessage(getResources().getString(R.string.print_error_code) + arg0);
                }
            });
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}
	}

	private String getCurTime(){
		Date date =new Date(System.currentTimeMillis());
		SimpleDateFormat format =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
		String time = format.format(date);
		return time;
	}

	public void printSmBuy(View v){
		try {
			String startTime = getCurTime();
			showMessage(getResources().getString(R.string.print_start_time)+startTime);
			printerDev.printText(new ArrayList<PrintItemObj>() {
				{
					add (new PrintItemObj ("POS purchase order", 8, true, PrintItemObj.ALIGN.CENTER));
					add (new PrintItemObj ("Merchant number: 00000000000 Terminal number: 100000000", 4, true, PrintItemObj.ALIGN.LEFT));
					add (new PrintItemObj ("Operator: 01 Acquirer: Lacala", 4, true, PrintItemObj.ALIGN.LEFT));
					add (new PrintItemObj ("Card number: 6214444 ****** 0095 1", 4, true, PrintItemObj.ALIGN.LEFT));
					add (new PrintItemObj ("Card Organization: Domestic Credit Card", 4, true, PrintItemObj.ALIGN.LEFT));
					add (new PrintItemObj ("Card Type: ICBC Validity Period: 20/12", 4, true, PrintItemObj.ALIGN.LEFT));
					add (new PrintItemObj ("Transaction Category: Consumption", 8, true, PrintItemObj.ALIGN.LEFT));
					add (new PrintItemObj ("Batch number: 000001", 4, true, PrintItemObj.ALIGN.LEFT));
					add (new PrintItemObj ("Credential No.:000033 Authorization No.:000000", 4, true, PrintItemObj.ALIGN.LEFT));
					add (new PrintItemObj ("Reference number: 1009000000033", 4, true, PrintItemObj.ALIGN.LEFT));
					add (new PrintItemObj ("Date / Time: 2017/10/10 11:11:11", 4, true, PrintItemObj.ALIGN.LEFT));
					add (new PrintItemObj ("Actual payment amount: RMB 100 yuan", 8, true, PrintItemObj.ALIGN.LEFT));
					add (new PrintItemObj ("Order amount: RMB 100 yuan", 4, true, PrintItemObj.ALIGN.LEFT));
					add (new PrintItemObj ("Promotion amount: RMB 0.00 yuan", 4, true, PrintItemObj.ALIGN.LEFT));
					add (new PrintItemObj ("Remarks:", 4, true, PrintItemObj.ALIGN.LEFT));
					add (new PrintItemObj ("AID: A000000333010101 TVR: 008004600:", 4, true, PrintItemObj.ALIGN.LEFT));
					add (new PrintItemObj ("ARQC: ABCDEFDGJHHHGA ATC: 0020:", 4, true, PrintItemObj.ALIGN.LEFT));
					add (new PrintItemObj ("Easy payment and convenient life:", 4, true, PrintItemObj.ALIGN.LEFT));
					add (new PrintItemObj ("Cardholder Signature:", 4, true, PrintItemObj.ALIGN.LEFT));
					add (new PrintItemObj ("I confirm the above transaction and agree to credit it to this card account", 4, true, PrintItemObj.ALIGN.LEFT));
					add (new PrintItemObj ("I ACKNOWLEDGE SATISFACTORY RECEIPT OF RELATIVE GOODS / SERVICES", 4, true, PrintItemObj.ALIGN.LEFT));
					add (new PrintItemObj ("-------------------------------------------- ---- ", 4, true, PrintItemObj.ALIGN.LEFT));
					add (new PrintItemObj ("SD_V2.1.8.11404.0 Customer Service Hotline: 95016", 4, true, PrintItemObj.ALIGN.LEFT));
					add (new PrintItemObj ("-------------------- Merchant Stub --------------------", 4, true, PrintItemObj.ALIGN.LEFT));					add(new PrintItemObj("\n\n\n"));
				}
			}, new AidlPrinterListener.Stub() {

				@Override
				public void onPrintFinish() throws RemoteException {
					String endTime = getCurTime();
					showMessage(getResources().getString(R.string.print_end_time)+endTime);
				}

				@Override
				public void onError(int arg0) throws RemoteException {
					showMessage(getResources().getString(R.string.print_error_code) + arg0);
				}
			});
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}
	}

	public void printBitmaps(View v){
		try {
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.pic_1);
			this.printerDev.printBmp(0, bitmap.getWidth(), bitmap.getHeight(), bitmap, mListen);

			Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(),R.drawable.pic_1);
			this.printerDev.printBmp(0, bitmap1.getWidth(), bitmap1.getHeight(), bitmap1, mListen);

			Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(),R.drawable.pic_1);
			this.printerDev.printBmp(0, bitmap2.getWidth(), bitmap2.getHeight(), bitmap2, mListen);


			this.printerDev.printText(new ArrayList<PrintItemObj>() {
				{
					add(new PrintItemObj("\n\n\n"));
				}
			},mListen);

		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	AidlPrinterListener mListen =new AidlPrinterListener.Stub() {
		@Override
		public void onError(int i) throws RemoteException {
			showMessage(getResources().getString(R.string.print_error_code) + i);
		}

		@Override
		public void onPrintFinish() throws RemoteException {
			showMessage(getResources().getString(R.string.print_success));
		}
	};
	public void showMessage(String str) {
		Toast.makeText(PrintDevActivity.this, ""+str, Toast.LENGTH_SHORT).show();

	}
	
}

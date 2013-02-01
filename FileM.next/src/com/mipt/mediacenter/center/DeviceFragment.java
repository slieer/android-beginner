package com.mipt.mediacenter.center;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mipt.fileMgr.R;
import com.mipt.fileMgr.center.FileMainActivity;
import com.mipt.mediacenter.center.server.DeviceInfo;
import com.mipt.mediacenter.center.server.MediacenterConstant;
import com.mipt.mediacenter.utils.ToastFactory;
import com.mipt.mediacenter.utils.Util;
import com.mipt.fileMgr.center.MainActivity;
/**
 * @author fang
 * @version $Id: 2013-01-21 09:26:01Z slieer $
 */
public class DeviceFragment extends Fragment implements
		MainActivity.DataChanged {
	private static final String TAG = "DeviceFragment";
	//private int tabId;
	private Activity mActivity;
	private DeviceAdapter adapter;
	private List<DeviceInfo> deviceList = new ArrayList<DeviceInfo>();
	private DeviceInfo currentDevice;
	private View rootView;
	private ListView listView;
	//private int backButtonId;
	private static final DeviceFragment f = new DeviceFragment();

	public static DeviceFragment newInstance(final int tabId,
			List<DeviceInfo> devs) {
		Bundle args = new Bundle();
		args.putSerializable(MediacenterConstant.INTENT_EXTRA, (ArrayList<DeviceInfo>)devs);
		f.setArguments(args);
		return f;
	}

	@Override
	public void onDataChanged(int _tabId,
			List<DeviceInfo> _devs) {
		dataChange(_devs);
		listView.setSelection(0);
	}

	public void dataChange(List<DeviceInfo> _devs) {
		if (adapter != null) {
			deviceList = _devs;
			adapter.notifyDataSetChanged();
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mActivity = this.getActivity();
		// this.getListView().setBackgroundResource(R.drawable.cm_view_background);
		rootView = inflater.inflate(R.layout.cm_device_list, container, false);
		listView = (ListView) rootView.findViewById(R.id.device_content);

		deviceList = (ArrayList<DeviceInfo>) (getArguments() != null ? getArguments()
				.getSerializable(MediacenterConstant.INTENT_EXTRA)
				: new ArrayList<DeviceInfo>());
		
		Log.i(TAG, "---deviceList.size:" + deviceList.size());
		adapter = new DeviceAdapter(this.getActivity(), R.layout.cm_device_item);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				final DeviceInfo file = adapter.getItem(arg2);
				currentDevice = file;
				if (file.isLive) {
					Util.runOnUiThread(mActivity, new Runnable() {
						@Override
						public void run() {
							Intent intent = new Intent(mActivity,
									FileMainActivity.class);
							intent.putExtra(MediacenterConstant.INTENT_EXTRA,
									file);
							//intent.putExtra(MediacenterConstant.INTENT_TYPE_VIEW, file.type);
							startActivity(intent);
						}

					});

				} else {
					ToastFactory
							.getInstance()
							.getToast(
									mActivity,
									mActivity
											.getString(R.string.cm_device_unlive_click)).show();
				}
			}

		});
		//listView.setNextFocusLeftId(backButtonId);
		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
	    super.onAttach(activity);
	    DeviceFragment f = (DeviceFragment) getFragmentManager()
                .findFragmentById(R.id.tabcontent);
	    MainActivity main = (MainActivity)getActivity();
	    main.onDataChanged(-1, null);
	    Log.i(TAG, "onAttach success..." + f);
	}
	
	public DeviceInfo getCurrentDevice() {
		return currentDevice;
	}

	public void runOnUiThread(Runnable r) {
		if (mActivity != null) {
			mActivity.runOnUiThread(r);
			// this.listView.getFocusedChild();
			// this.listView.setSelection(0);
		}

	}

	class DeviceAdapter extends BaseAdapter {
		private static final String TAG = "FileListAdapter";
		private LayoutInflater mInflater;
		private Context cxt;

		public DeviceAdapter(Context context, int resource) {
			cxt = context;
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.cm_device_item, null);
				holder = new ViewHolder();
				holder.devImg = (ImageView) convertView
						.findViewById(R.id.dev_img);
				holder.devName = (TextView) convertView
						.findViewById(R.id.dev_name);
				holder.percent = (TextView) convertView
						.findViewById(R.id.dev_percent_desc);
				holder.dlanDesc = (TextView) convertView
						.findViewById(R.id.dlan_des);
				holder.devStatus = (TextView) convertView
						.findViewById(R.id.dev_des_title);
				holder.pbar = (ProgressBar) convertView
						.findViewById(R.id.dev_progressBar);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (deviceList.get(position) != null) {
				DeviceInfo device = deviceList.get(position);
				holder.devName.setText(device.devName);
				holder.devImg.setImageResource(device.resId);
				if (!device.isLive) {
					int color = getResources().getColor(
							R.color.cm_device_unused);
					holder.devName.setTextColor(color);
					holder.devStatus.setTextColor(color);
					holder.devStatus.setText(cxt
							.getString(R.string.cm_usb_desc));
					holder.devStatus.setVisibility(View.VISIBLE);
					holder.dlanDesc.setVisibility(View.GONE);

					holder.pbar.setVisibility(View.GONE);
					holder.percent.setVisibility(View.GONE);
				} else {
					holder.devName.setTextColor(Color.WHITE);
					holder.devStatus.setTextColor(Color.WHITE);
/*					if (device.type == DeviceInfo.TYPE_DLAN) {
					    
					} else {*/
                    holder.devStatus.setVisibility(View.GONE);
                    holder.pbar.setVisibility(View.VISIBLE);
                    holder.dlanDesc.setVisibility(View.GONE);
                    int total = (int)(device.devSize / 10000000);
                    int used = (int)(device.devUsedSize / 10000000);
                    holder.pbar.setMax(total);
                    holder.pbar.setProgress(used);
                    holder.percent.setText(Util.convertStorage(device.devSize - device.devUsedSize)
                            + getString(R.string.cm_device_progress)
                            + Util.convertStorage(device.devSize));
                    holder.percent.setVisibility(View.VISIBLE);
					//}

				}

			}
			return convertView;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (deviceList != null) {
				return deviceList.size();
			} else
				return 0;
		}

		@Override
		public DeviceInfo getItem(int position) {
			// TODO Auto-generated method stub
			return deviceList == null || deviceList.isEmpty() ? null
					: deviceList.get(position);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		class ViewHolder {
			ImageView devImg;
			TextView devName;
			TextView devStatus;
			TextView dlanDesc;
			TextView percent;
			ProgressBar pbar;
		}
	}

}

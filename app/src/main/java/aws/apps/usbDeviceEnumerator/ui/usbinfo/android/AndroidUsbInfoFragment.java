/*******************************************************************************
 * Copyright 2011 Alexandros Schillings
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package aws.apps.usbDeviceEnumerator.ui.usbinfo.android;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import aws.apps.usbDeviceEnumerator.R;
import aws.apps.usbDeviceEnumerator.data.DataProviderCompanyInfo;
import aws.apps.usbDeviceEnumerator.data.DataProviderCompanyLogo;
import aws.apps.usbDeviceEnumerator.data.DataProviderUsbInfo;
import aws.apps.usbDeviceEnumerator.ui.usbinfo.common.BaseInfoFragment;
import aws.apps.usbDeviceEnumerator.ui.usbinfo.common.DataFetcher;
import aws.apps.usbDeviceEnumerator.ui.usbinfo.common.InfoViewHolder;
import aws.apps.usbDeviceEnumerator.ui.usbinfo.common.InfoViewLoader;

public class AndroidUsbInfoFragment extends BaseInfoFragment {
    private static final String DEFAULT_STRING = "???";
    private static final String EXTRA_DATA = AndroidUsbInfoFragment.class.getName() + ".BUNDLE_DATA";
    private static final String TAG = AndroidUsbInfoFragment.class.getName();
    private static final int LAYOUT_ID = R.layout.fragment_usb_info;

    private String usbKey = DEFAULT_STRING;
    private InfoViewHolder viewHolder;
    private UsbManager usbMan;
    private DataFetcher dataFetcher;
    private UsbDevice device;
    private boolean validData;

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);

        usbMan = (UsbManager) getContext().getSystemService(Context.USB_SERVICE);
        dataFetcher = new DataFetcher(
                new DataProviderCompanyInfo(context),
                new DataProviderUsbInfo(context),
                new DataProviderCompanyLogo(context));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saved) {
        usbKey = getArguments().getString(EXTRA_DATA, DEFAULT_STRING);
        device = usbMan.getDeviceList().get(usbKey);

        final View view;

        if (usbKey == null || device == null) {
            view = inflater.inflate(R.layout.fragment_error, container, false);
            validData = false;
        } else {
            view = inflater.inflate(LAYOUT_ID, container, false);
            validData = true;
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        viewHolder = new InfoViewHolder(view);

        usbKey = getArguments().getString(EXTRA_DATA, DEFAULT_STRING);

        if (validData) {
            viewHolder = new InfoViewHolder(view);
            final InfoViewLoader.UiRunner uiRunner = new InfoViewLoader.UiRunner(this);
            final ViewLoader loader
                    = new ViewLoader(LayoutInflater.from(getContext()), getResources(), dataFetcher, uiRunner);
            loader.populateDataTable(viewHolder, usbKey, device);
        } else {
            final TextView textView = (TextView) view.findViewById(R.id.errorText);
            if (usbKey == null) {
                textView.setText(R.string.error_loading_device_info_unknown);
            } else {
                textView.setText(R.string.error_loading_device_info_device_disconnected);
            }
        }
    }

    @Override
    public InfoViewHolder getViewHolder() {
        return viewHolder;
    }

    public static Fragment create(final String usbKey) {
        final Fragment fragment = new AndroidUsbInfoFragment();
        final Bundle bundle = new Bundle();
        bundle.putString(EXTRA_DATA, usbKey);
        fragment.setArguments(bundle);
        return fragment;
    }
}

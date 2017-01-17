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
package aws.apps.usbDeviceEnumerator.ui.usbinfo.linux;

import android.content.Context;
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
import uk.co.alt236.usbdeviceenumerator.sysbususb.SysBusUsbDevice;

public class LinuxUsbInfoFragment extends BaseInfoFragment {
    public static final String DEFAULT_STRING = "???";

    private static final String EXTRA_DATA = LinuxUsbInfoFragment.class.getName() + ".BUNDLE_DATA";
    private static final int LAYOUT_ID = R.layout.fragment_usb_info;
    private static final String TAG = LinuxUsbInfoFragment.class.getName();

    private SysBusUsbDevice device;
    private boolean validData;
    private InfoViewHolder viewHolder;
    private DataFetcher dataFetcher;

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        dataFetcher = new DataFetcher(
                new DataProviderCompanyInfo(context),
                new DataProviderUsbInfo(context),
                new DataProviderCompanyLogo(context));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saved) {
        device = getArguments().getParcelable(EXTRA_DATA);
        final View view;

        if (device == null) {
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

        if (validData) {
            viewHolder = new InfoViewHolder(view);
            final InfoViewLoader.UiRunner uiRunner = new InfoViewLoader.UiRunner(this);
            final ViewLoader loader
                    = new ViewLoader(LayoutInflater.from(getContext()), getResources(), dataFetcher, uiRunner);
            loader.populateDataTable(viewHolder, device);
        } else {
            final TextView textView = (TextView) view.findViewById(R.id.errorText);
            textView.setText(R.string.error_loading_device_info_unknown);
        }
    }

    @Override
    public InfoViewHolder getViewHolder() {
        return viewHolder;
    }

    public static Fragment create(final SysBusUsbDevice usbDevice) {
        final Fragment fragment = new LinuxUsbInfoFragment();
        final Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_DATA, usbDevice);
        fragment.setArguments(bundle);
        return fragment;
    }
}

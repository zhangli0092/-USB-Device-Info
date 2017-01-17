package aws.apps.usbDeviceEnumerator.ui.usbinfo.linux;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.widget.TableLayout;

import aws.apps.usbDeviceEnumerator.R;
import aws.apps.usbDeviceEnumerator.ui.usbinfo.common.DataFetcher;
import aws.apps.usbDeviceEnumerator.ui.usbinfo.common.InfoViewHolder;
import aws.apps.usbDeviceEnumerator.ui.usbinfo.common.InfoViewLoader;
import uk.co.alt236.usbdeviceenumerator.UsbConstantResolver;
import uk.co.alt236.usbdeviceenumerator.sysbususb.SysBusUsbDevice;

/*package*/ class ViewLoader extends InfoViewLoader {
    private final LayoutInflater inflater;
    private final Resources resources;
    private final DataFetcher dataFetcher;
    private final UiRunner uiRunner;

    ViewLoader(final LayoutInflater layoutInflater,
               final Resources resources,
               final DataFetcher dataFetcher,
               final UiRunner uiRunner) {

        super(resources, uiRunner, dataFetcher);
        this.inflater = layoutInflater;
        this.resources = resources;
        this.dataFetcher = dataFetcher;
        this.uiRunner = uiRunner;
    }

    public void populateDataTable(InfoViewHolder viewHolder,
                                  SysBusUsbDevice device) {

        final String vid = padLeft(device.getVid(), "0", 4);
        final String pid = padLeft(device.getPid(), "0", 4);
        final String deviceClass = UsbConstantResolver.resolveUsbClass(device);

        viewHolder.getLogo().setImageResource(R.drawable.no_image);

        viewHolder.getVid().setText(vid);
        viewHolder.getPid().setText(pid);
        viewHolder.getDevicePath().setText(device.getDevicePath());
        viewHolder.getDeviceClass().setText(deviceClass);

        viewHolder.getReportedVendor().setText(device.getReportedVendorName());
        viewHolder.getReportedProduct().setText(device.getReportedProductName());

        final TableLayout bottomTable = viewHolder.getBottomTable();
        addDataRow(inflater, bottomTable, resources.getString(R.string.usb_version_), device.getUsbVersion());
        addDataRow(inflater, bottomTable, resources.getString(R.string.speed_), device.getSpeed());
        addDataRow(inflater, bottomTable, resources.getString(R.string.protocol_), device.getDeviceProtocol());
        addDataRow(inflater, bottomTable, resources.getString(R.string.maximum_power_), device.getMaxPower());
        addDataRow(inflater, bottomTable, resources.getString(R.string.serial_number_), device.getSerialNumber());

        loadAsyncData(viewHolder, vid, pid, device.getReportedVendorName());
    }
}

package aws.apps.usbDeviceEnumerator.ui.usbinfo.android;

import android.content.res.Resources;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.widget.TableLayout;

import aws.apps.usbDeviceEnumerator.R;
import aws.apps.usbDeviceEnumerator.ui.usbinfo.common.DataFetcher;
import aws.apps.usbDeviceEnumerator.ui.usbinfo.common.InfoViewHolder;
import aws.apps.usbDeviceEnumerator.ui.usbinfo.common.InfoViewLoader;
import uk.co.alt236.usbdeviceenumerator.UsbConstantResolver;

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
                                  String usbKey,
                                  UsbDevice device) {

        final String vid = padLeft(Integer.toHexString(device.getVendorId()), "0", 4);
        final String pid = padLeft(Integer.toHexString(device.getProductId()), "0", 4);
        final String deviceClass = UsbConstantResolver.resolveUsbClass(device.getDeviceClass());

        viewHolder.getLogo().setImageResource(R.drawable.no_image);

        viewHolder.getVid().setText(vid);
        viewHolder.getPid().setText(pid);
        viewHolder.getDevicePath().setText(usbKey);
        viewHolder.getDeviceClass().setText(deviceClass);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            viewHolder.getReportedVendor().setText(device.getManufacturerName());
            viewHolder.getReportedProduct().setText(device.getProductName());
        } else {
            viewHolder.getReportedVendor().setText(R.string.not_provided);
            viewHolder.getReportedProduct().setText(R.string.not_provided);
        }

        UsbInterface iFace;
        for (int i = 0; i < device.getInterfaceCount(); i++) {
            iFace = device.getInterface(i);
            if (iFace != null) {
                final TableLayout bottomTable = viewHolder.getBottomTable();
                final String usbClass = UsbConstantResolver.resolveUsbClass((iFace.getInterfaceClass()));

                addDataRow(inflater, bottomTable, resources.getString(R.string.interface_) + i, "");
                addDataRow(inflater, bottomTable, resources.getString(R.string.class_), usbClass);

                if (iFace.getEndpointCount() > 0) {
                    String endpointText;
                    for (int j = 0; j < iFace.getEndpointCount(); j++) {
                        endpointText = getEndpointText(iFace.getEndpoint(j), j);
                        addDataRow(inflater, bottomTable, resources.getString(R.string.endpoint_), endpointText);
                    }
                } else {
                    addDataRow(inflater, bottomTable, "\tEndpoints:", "none");
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            loadAsyncData(viewHolder, vid, pid, device.getManufacturerName());
        } else {
            loadAsyncData(viewHolder, vid, pid, null);
        }
    }

    private String getEndpointText(final UsbEndpoint endpoint, final int index) {
        final String addressInBinary = padLeft(Integer.toBinaryString(endpoint.getAddress()), "0", 8);
        final String addressInHex = padLeft(Integer.toHexString(endpoint.getAddress()), "0", 2);
        final String attributesInBinary = padLeft(Integer.toBinaryString(endpoint.getAttributes()), "0", 8);

        String endpointText = "#" + index + "\n";
        endpointText += resources.getString(R.string.address_) + "0x" + addressInHex + " (" + addressInBinary + ")\n";
        endpointText += resources.getString(R.string.number_) + endpoint.getEndpointNumber() + "\n";
        endpointText += resources.getString(R.string.direction_) + UsbConstantResolver.resolveUsbEndpointDirection(endpoint.getDirection()) + "\n";
        endpointText += resources.getString(R.string.type_) + UsbConstantResolver.resolveUsbEndpointType(endpoint.getType()) + "\n";
        endpointText += resources.getString(R.string.poll_interval_) + endpoint.getInterval() + "\n";
        endpointText += resources.getString(R.string.max_packet_size_) + endpoint.getMaxPacketSize() + "\n";
        endpointText += resources.getString(R.string.attributes_) + attributesInBinary;

        return endpointText;
    }
}

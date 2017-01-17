package aws.apps.usbDeviceEnumerator.ui.usbinfo.common;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import aws.apps.usbDeviceEnumerator.R;

public abstract class InfoViewLoader {

    private final Resources resources;
    private final UiRunner uiRunner;
    private final DataFetcher dataFetcher;

    protected InfoViewLoader(final Resources resources,
                             final UiRunner uiRunner,
                             final DataFetcher dataFetcher) {
        this.resources = resources;
        this.uiRunner = uiRunner;
        this.dataFetcher = dataFetcher;
    }

    protected void addDataRow(LayoutInflater inflater,
                              TableLayout tlb,
                              String cell1Text,
                              String cell2Text) {
        final TableRow row = (TableRow) inflater.inflate(R.layout.usb_table_row_data, null);
        final TextView tv1 = (TextView) row.findViewById(R.id.usb_tablerow_cell1);
        final TextView tv2 = (TextView) row.findViewById(R.id.usb_tablerow_cell2);
        tv1.setText(cell1Text);
        tv2.setText(cell2Text);
        tlb.addView(row);
    }

    protected String padLeft(String string, String padding, int size) {
        String pad = "";
        while ((pad + string).length() < size) {
            pad += padding + pad;
        }
        return pad + string;
    }

    protected void loadAsyncData(final InfoViewHolder viewHolder,
                                 final String vid,
                                 final String pid,
                                 final String reportedVendorName) {

        final DataFetcher.Callback callback = new PopulateAsyncDataCallback(viewHolder, resources, uiRunner);
        dataFetcher.fetchData(vid, pid, reportedVendorName, callback);
    }

    public static class UiRunner {
        final Fragment fragment;

        public UiRunner(final Fragment fragment) {
            this.fragment = fragment;
        }

        public void runOnUiThread(final Runnable runnable) {
            if (fragment.isAdded()
                    && fragment.getActivity() != null
                    && fragment.getView() != null) {
                fragment.getActivity().runOnUiThread(runnable);
            }
        }
    }

    private static class PopulateAsyncDataCallback implements DataFetcher.Callback {
        private final InfoViewHolder viewHolder;
        private final Resources resources;
        private final UiRunner uiRunner;

        public PopulateAsyncDataCallback(final InfoViewHolder viewHolder,
                                         final Resources resources,
                                         final UiRunner uiRunner) {
            this.viewHolder = viewHolder;
            this.resources = resources;
            this.uiRunner = uiRunner;
        }

        @Override
        public void onSuccess(final String vendorFromDb,
                              final String productFromDb,
                              final Bitmap bitmap) {

            uiRunner.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    viewHolder.getVendorFromDb().setText(vendorFromDb);
                    viewHolder.getProductFromDb().setText(productFromDb);
                    if (bitmap != null) {
                        final BitmapDrawable drawable = new BitmapDrawable(resources, bitmap);
                        viewHolder.getLogo().setImageDrawable(drawable);
                    } else {
                        viewHolder.getLogo().setImageResource(R.drawable.no_image);
                    }
                }
            });
        }
    }
}

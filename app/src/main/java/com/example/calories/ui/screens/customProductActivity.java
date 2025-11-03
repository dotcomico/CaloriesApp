package com.example.calories.ui.screens;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.calories.BaseActivity;
import com.example.calories.data.models.Product;
import com.example.calories.ui.dialogs.BarcodeDialogHandler;
import com.example.calories.ui.views.UnitSelectorView;
import com.example.calories.export.ProductExporter;
import com.example.calories.ui.adapters.ProductItemAdapter;
import com.example.calories.ui.adapters.ProductItemDeletionHelper;
import com.example.calories.data.storage.ProductStorageManager;
import com.example.calories.R;
import com.example.calories.ui.adapters.RecyclerItemClickListener;
import com.example.calories.utils.Utility;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

import static com.example.calories.utils.AppConstants.*;

public class customProductActivity extends BaseActivity
		implements View.OnClickListener, AdapterView.OnItemSelectedListener {
	private ArrayList<Product> customProducts = new ArrayList<>();
	private Product product;
	private ProductItemAdapter productItemAdapter;
	private RecyclerView recyclerView;
	private Button updateProductButton;
	private LinearLayout editProductLayout;
	private ImageView cancelEditImageView, scanBarcodeImageView;
	private EditText productNameEditText, productCaloriesEditText;
	private int currentMenuResourceId = R.menu.pa_menu;
	private int lastClickedItemPosition = 0;
	private ProductStorageManager productStorageManager;
	private BarcodeDialogHandler barcodeDialogHandler;
	private ProductItemDeletionHelper deletionHelper;
	private UnitSelectorView unitSelectorView;
	private static ScreenCloseListener screenCloseListener;
	private FloatingActionButton fab;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_custom_product);

//       Objects.requireNonNull(getSupportActionBar()).show();

		productStorageManager = new ProductStorageManager(this);
		barcodeDialogHandler = new BarcodeDialogHandler(this);

		initViews();
		setRecyclerView();
		initListeners();
	}

	private void initListeners() {
		fab.setOnClickListener(
				view -> startActivity(new Intent(customProductActivity.this, ProductCreationActivity.class)));

		ProductStorageManager.setGlobalProductCreatedListener(newProduct -> {
			// ברגע שנוצר מוצר חדש, תרענן את הרשימה
			refreshRecyclerView();
		});
	}

	private void initViews() {
		fab = findViewById(R.id.fab);
		recyclerView = findViewById(R.id.productsRecyclerView);
		editProductLayout = findViewById(R.id.layoutEditProduct);
		cancelEditImageView = findViewById(R.id.iv_delete);
		scanBarcodeImageView = findViewById(R.id.iv_barcodeScan);
		productNameEditText = findViewById(R.id.et_food);
		productCaloriesEditText = findViewById(R.id.et_kal);
		unitSelectorView = findViewById(R.id.unit_selector);
		updateProductButton = findViewById(R.id.btn_save);

		scanBarcodeImageView.setOnClickListener(this);
		cancelEditImageView.setOnClickListener(this);
		updateProductButton.setOnClickListener(this);
	}

	public interface ScreenCloseListener {
		void onScreenClosed();
	}

	public static void setScreenCloseListener(ScreenCloseListener listener) {
		screenCloseListener = listener;
	}

	private void setRecyclerView() {

		customProducts = productStorageManager.load();

		recyclerView.setLayoutManager(new LinearLayoutManager(this));

		productItemAdapter = new ProductItemAdapter(customProducts);
		recyclerView.setAdapter(productItemAdapter);

		deletionHelper = new ProductItemDeletionHelper(this, customProducts, productItemAdapter,
				() -> changeMenu(deletionHelper.isInDeleteMode() ? R.menu.delete_item_menu : R.menu.pa_menu));

		recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(customProductActivity.this, recyclerView,
				new RecyclerItemClickListener.OnItemClickListener() {
					@Override
					public void onItemClick(View view, int position) {
						if (deletionHelper.isInDeleteMode()) {
							deletionHelper.toggleItemSelection(position);
						} else {
							// מצב רגיל - עריכת פריט
							product = customProducts.get(position);

							lastClickedItemPosition = position;

							editProductLayout.setVisibility(View.VISIBLE);

							productNameEditText.setText(product.getName());
							productCaloriesEditText.setText(product.getCalorieText());
							unitSelectorView.setSelectedUnit(product.getUnit());

							barcodeDialogHandler.getBarcodeEditText()
									.setText(product.getBarcode() != null ? product.getBarcode() : "");
						}
					}

					@Override
					public void onLongItemClick(View view, int position) {
						deletionHelper.enterDeleteMode(position);
					}
				}));

	}

	private void refreshRecyclerView() {
		customProducts = productStorageManager.load();
		productItemAdapter = new ProductItemAdapter(customProducts);
		recyclerView.setAdapter(productItemAdapter);
	}

	public void changeMenu(int myMenu) {
		currentMenuResourceId = myMenu;
		invalidateOptionsMenu();
	}

	private void dotanAll() {

		customProducts.add(new Product(1, "נס קפה קר,  חלב סויה וניל", UNIT_CUP, "90", ""));
		customProducts.add(new Product(1, "נס קפה קר", UNIT_CUP, "100", ""));
		customProducts.add(new Product(1, "ציפס תפוח אדמה בנינגה", UNIT_UNIT, "8", ""));
		customProducts.add(new Product(1, "בורקס בשר 250 גרם", UNIT_UNIT, "625", ""));
		customProducts.add(new Product(1, "בולגרית יוונית לבן", "קוביה", "14", ""));
		customProducts.add(new Product(1, "בולגרית יוונית לבן", UNIT_100_GRAM, "282", ""));
		customProducts.add(new Product(1, "טונה ירקות- עגבניה שום בצל פטריות תבלינים", UNIT_UNIT, "200", ""));
		customProducts.add(new Product(1, "טונה ירקות של דותן", UNIT_100_GRAM, "91", ""));
		customProducts.add(new Product(1, "מרק ירקות- תפוד גזר בצל פקק שמן ותבלינים", "סיר קטן", "220", ""));
		customProducts.add(new Product(1, "טוסט גבינה 28 אחוז", UNIT_UNIT, "250", ""));
		customProducts.add(new Product(1, "קציצת גונדי", UNIT_UNIT, "60", ""));
		customProducts.add(new Product(1, "המבורגר דאבל וציפס", "מנה", "900", ""));
		customProducts.add(new Product(1, "טוסט חיילים-חצי פיתה", UNIT_UNIT, "350", ""));

		productStorageManager.save(customProducts);

		Utility.makeToast("נוסף למערכת! -המוצרים השמורים של דותן", getBaseContext());
	}

	private void printAll() {
		ProductExporter exporter = new ProductExporter(this);
		exporter.export(customProducts);
	}

	@Override
	public void onClick(View view) {
		if (view == updateProductButton) {

			Product updatedProduct = customProducts.get(lastClickedItemPosition);

			updatedProduct.setCalorieText(productCaloriesEditText.getText().toString().trim());
			updatedProduct.setName(productNameEditText.getText().toString().trim());
			updatedProduct.setBarcode(barcodeDialogHandler.getBarcodeEditText().getText().toString().trim());

			String measurement = unitSelectorView.getUnit();
			if (!measurement.isEmpty()) {
				updatedProduct.setUnit(measurement);
			}

			productItemAdapter = new ProductItemAdapter(customProducts);

			recyclerView.setAdapter(productItemAdapter);

			productStorageManager.save(customProducts);

			editProductLayout.setVisibility(View.GONE);
		}
		if (view == scanBarcodeImageView) {
			barcodeDialogHandler.showDialog();
		}
		if (view == cancelEditImageView) {
			cancelEdit();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
		if (barcodeDialogHandler != null && result != null) {
			barcodeDialogHandler.handleActivityResult(result);
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	private void cancelEdit() {
		editProductLayout.setVisibility(View.GONE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(currentMenuResourceId, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu1_back) {
			finish();
			return true;
		}

		if (item.getItemId() == R.id.menu1_dotan) {
			dotanAll();
			recyclerView.setAdapter(productItemAdapter);
			return true;
		}

		if (item.getItemId() == R.id.menu1_print_all) {
			printAll();
			return true;
		}

		if (item.getItemId() == R.id.menu_abortDelete) {
			deletionHelper.exitDeleteMode();
			return true;
		}

		if (item.getItemId() == R.id.menu_delete) {
			deletionHelper.deleteSelectedItems();
			productStorageManager.save(customProducts);
			return true;
		}

		if (item.getItemId() == R.id.menu_select_all) {
			deletionHelper.toggleSelectAll();
			return true;
		}

		return super.onOptionsItemSelected(item);

	}

	@Override
	public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

	}

	@Override
	public void onNothingSelected(AdapterView<?> adapterView) {

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (screenCloseListener != null) {
			screenCloseListener.onScreenClosed();
		}
	}

}
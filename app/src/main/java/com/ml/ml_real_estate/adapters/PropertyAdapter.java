package com.ml.ml_real_estate.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ml.ml_real_estate.R;
import com.ml.ml_real_estate.models.Property;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class PropertyAdapter extends RecyclerView.Adapter<PropertyAdapter.PropertyViewHolder> {
    private static final String TAG = "PropertyAdapter";

    private List<Property> propertyList;
    private Context context;
    private boolean isFavoriteFragment;

    public PropertyAdapter(List<Property> propertyList, Context context, boolean isFavoriteFragment) {
        this.propertyList = propertyList;
        this.context = context;
        this.isFavoriteFragment = isFavoriteFragment;
        Log.d(TAG, "PropertyAdapter initialized with " + propertyList.size() + " properties");
    }

    @NonNull
    @Override
    public PropertyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "Creating view holder");
        try {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_property, parent, false);
            return new PropertyViewHolder(view);
        } catch (Exception e) {
            Log.e(TAG, "Error creating view holder: " + e.getMessage());
            return createEmergencyViewHolder(parent);
        }
    }

    private PropertyViewHolder createEmergencyViewHolder(ViewGroup parent) {
        Log.d(TAG, "Creating emergency view holder");
        android.widget.LinearLayout layout = new android.widget.LinearLayout(parent.getContext());
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);

        TextView textView = new TextView(parent.getContext());
        textView.setText("Property Item");
        textView.setTextSize(16);

        Button buyButton = new Button(parent.getContext());
        buyButton.setText("Buy Now");
        buyButton.setPadding(20, 10, 20, 10);

        layout.addView(textView);
        layout.addView(buyButton);

        return new PropertyViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull PropertyViewHolder holder, int position) {
        Log.d(TAG, "Binding view holder for position: " + position);

        try {
            Property property = propertyList.get(position);
            holder.bind(property);
        } catch (Exception e) {
            Log.e(TAG, "Error binding view holder: " + e.getMessage());
            holder.bindEmergency();
        }
    }

    @Override
    public int getItemCount() {
        return propertyList.size();
    }

    public void updateData(List<Property> newProperties) {
        this.propertyList = newProperties;
        notifyDataSetChanged();
    }

    public class PropertyViewHolder extends RecyclerView.ViewHolder {
        private ImageView propertyImage;
        private TextView propertyTitle, propertyPrice, propertyLocation, propertyType, propertyDetails;
        private Button btnBuyNow, btnFavorite;
        private View itemView;

        public PropertyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            Log.d(TAG, "PropertyViewHolder constructor");

            try {
                propertyImage = itemView.findViewById(R.id.propertyImage);
                propertyTitle = itemView.findViewById(R.id.propertyTitle);
                propertyPrice = itemView.findViewById(R.id.propertyPrice);
                propertyLocation = itemView.findViewById(R.id.propertyLocation);
                propertyType = itemView.findViewById(R.id.propertyType);
                propertyDetails = itemView.findViewById(R.id.propertyDetails);
                btnBuyNow = itemView.findViewById(R.id.btnBuyNow);
                btnFavorite = itemView.findViewById(R.id.btnFavorite);

                Log.d(TAG, "Views initialized - BuyNow: " + (btnBuyNow != null));

            } catch (Exception e) {
                Log.e(TAG, "Error in PropertyViewHolder constructor: " + e.getMessage());
            }
        }

        public void bind(Property property) {
            Log.d(TAG, "Binding property: " + property.getTitle());

            try {
                // Set property data
                if (propertyTitle != null) {
                    propertyTitle.setText(property.getTitle());
                }

                if (propertyPrice != null) {
                    NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
                    String formattedPrice = format.format(property.getPrice());
                    propertyPrice.setText(formattedPrice);
                }

                if (propertyLocation != null) {
                    // Use the address from the new Property model
                    String location = "Location not available";
                    if (property.getAddress() != null) {
                        location = property.getAddress().getCity() + ", " + property.getAddress().getState();
                    }
                    propertyLocation.setText(location);
                }

                if (propertyType != null) {
                    propertyType.setText(property.getPropertyType() + " • " + property.getListingType());
                }

                if (propertyDetails != null) {
                    String details = property.getBedrooms() + " beds • " +
                            property.getBathrooms() + " baths • " +
                            property.getAreaSqft() + " sqft";
                    propertyDetails.setText(details);
                }

                // Load image with Glide - using primaryImage from new model
                if (propertyImage != null) {
                    try {
                        String imageUrl = property.getPrimaryImage();
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            Glide.with(context)
                                    .load(imageUrl)
                                    .placeholder(R.drawable.ic_house_placeholder)
                                    .error(R.drawable.ic_house_placeholder)
                                    .into(propertyImage);
                        } else {
                            // Set default placeholder if no image
                            propertyImage.setImageResource(R.drawable.ic_house_placeholder);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error loading image: " + e.getMessage());
                        propertyImage.setImageResource(R.drawable.ic_house_placeholder);
                    }
                }

                // Setup Buy Now button
                if (btnBuyNow != null) {
                    btnBuyNow.setOnClickListener(v -> handleBuyNow(property));
                    btnBuyNow.setVisibility(View.VISIBLE);

                    // Update button text based on listing type
                    if ("rent".equalsIgnoreCase(property.getListingType())) {
                        btnBuyNow.setText("Rent Now");
                    } else if ("lease".equalsIgnoreCase(property.getListingType())) {
                        btnBuyNow.setText("Lease Now");
                    } else {
                        btnBuyNow.setText("Buy Now");
                    }

                    Log.d(TAG, "Buy Now button setup for: " + property.getTitle());
                } else {
                    Log.w(TAG, "Buy Now button is null");
                }

                // Setup Favorite button - simplified since we don't have isFavorite in model yet
                if (btnFavorite != null) {
                    btnFavorite.setVisibility(isFavoriteFragment ? View.GONE : View.VISIBLE);
                    // For now, just show heart outline
                    btnFavorite.setText("🤍");
                    btnFavorite.setOnClickListener(v -> toggleFavorite(property));
                }

                // Make entire item clickable
                itemView.setOnClickListener(v -> {
                    Log.d(TAG, "Property clicked: " + property.getTitle());
                    showPropertyDetails(property);
                });

            } catch (Exception e) {
                Log.e(TAG, "Error binding property: " + e.getMessage());
                bindEmergency();
            }
        }

        private void handleBuyNow(Property property) {
            Log.d(TAG, "Buy Now clicked for: " + property.getTitle());

            try {
                String actionText = "rent".equalsIgnoreCase(property.getListingType()) ? "Rent" :
                        "lease".equalsIgnoreCase(property.getListingType()) ? "Lease" : "Buy";

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
                builder.setTitle(actionText + " Property");
                builder.setMessage("Are you sure you want to " + actionText.toLowerCase() + " \"" +
                        property.getTitle() + "\" for " +
                        NumberFormat.getCurrencyInstance(Locale.US).format(property.getPrice()) +
                        ("rent".equalsIgnoreCase(property.getListingType()) ? "/month?" : "?"));

                builder.setPositiveButton("Confirm " + actionText, (dialog, which) -> {
                    processPurchase(property, actionText);
                });

                builder.setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                });

                builder.setNeutralButton("More Info", (dialog, which) -> {
                    showPropertyDetails(property);
                });

                builder.show();

            } catch (Exception e) {
                Log.e(TAG, "Error in handleBuyNow: " + e.getMessage());
                Toast.makeText(context, "Purchase feature unavailable", Toast.LENGTH_SHORT).show();
            }
        }

        private void processPurchase(Property property, String action) {
            Log.d(TAG, "Processing " + action.toLowerCase() + " for: " + property.getTitle());

            try {
                Toast.makeText(context, "Processing your request...", Toast.LENGTH_SHORT).show();

                new android.os.Handler().postDelayed(() -> {
                    android.app.AlertDialog.Builder successBuilder = new android.app.AlertDialog.Builder(context);
                    successBuilder.setTitle("🎉 " + action + " Request Successful!");
                    successBuilder.setMessage("Congratulations! Your " + action.toLowerCase() + " request for:\n\n" +
                            property.getTitle() + "\n" +
                            (property.getAddress() != null ? property.getAddress().getCity() + ", " + property.getAddress().getState() : "") + "\n" +
                            "Price: " + NumberFormat.getCurrencyInstance(Locale.US).format(property.getPrice()) +
                            ("rent".equalsIgnoreCase(property.getListingType()) ? "/month" : "") + "\n\n" +
                            "Our team will contact you shortly to complete the process.");
                    successBuilder.setPositiveButton("Great!", (dialog, which) -> {
                        dialog.dismiss();
                    });
                    successBuilder.show();

                    Log.d(TAG, action + " request completed for: " + property.getTitle());

                }, 2000);

            } catch (Exception e) {
                Log.e(TAG, "Error processing " + action.toLowerCase() + ": " + e.getMessage());
                Toast.makeText(context, action + " failed. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }

        private void toggleFavorite(Property property) {
            try {
                // TODO: Implement actual favorite functionality with API
                Toast.makeText(context, "Favorite feature coming soon!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e(TAG, "Error toggling favorite: " + e.getMessage());
            }
        }

        private void showPropertyDetails(Property property) {
            try {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
                builder.setTitle(property.getTitle());

                String location = "Location not available";
                if (property.getAddress() != null) {
                    location = property.getAddress().getFullAddress();
                }

                String message = "📍 " + location + "\n\n" +
                        "💰 Price: " + NumberFormat.getCurrencyInstance(Locale.US).format(property.getPrice()) +
                        ("rent".equalsIgnoreCase(property.getListingType()) ? "/month" : "") + "\n" +
                        "🏠 Type: " + property.getPropertyType() + "\n" +
                        "📋 Listing: " + property.getListingType() + "\n" +
                        "🛏️ Bedrooms: " + property.getBedrooms() + "\n" +
                        "🚿 Bathrooms: " + property.getBathrooms() + "\n" +
                        "📏 Area: " + property.getAreaSqft() + " sqft\n";

                if (property.getYearBuilt() != null && !property.getYearBuilt().isEmpty()) {
                    message += "🏗️ Year Built: " + property.getYearBuilt() + "\n";
                }

                if (property.getSellerName() != null && !property.getSellerName().isEmpty()) {
                    message += "👤 Seller: " + property.getSellerName() + "\n";
                }

                message += "\n" + property.getDescription();

                builder.setMessage(message);

                String actionText = "rent".equalsIgnoreCase(property.getListingType()) ? "Rent Now" :
                        "lease".equalsIgnoreCase(property.getListingType()) ? "Lease Now" : "Buy Now";

                builder.setPositiveButton(actionText, (dialog, which) -> {
                    handleBuyNow(property);
                });

                builder.setNegativeButton("Close", (dialog, which) -> {
                    dialog.dismiss();
                });

                builder.show();
            } catch (Exception e) {
                Log.e(TAG, "Error showing property details: " + e.getMessage());
            }
        }

        public void bindEmergency() {
            Log.d(TAG, "Using emergency bind");
            try {
                if (itemView instanceof android.widget.LinearLayout) {
                    android.widget.LinearLayout layout = (android.widget.LinearLayout) itemView;
                    if (layout.getChildCount() > 1) {
                        View secondChild = layout.getChildAt(1);
                        if (secondChild instanceof Button) {
                            Button emergencyBuyButton = (Button) secondChild;
                            emergencyBuyButton.setOnClickListener(v -> {
                                Toast.makeText(context, "Buy option selected", Toast.LENGTH_SHORT).show();
                            });
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in emergency bind: " + e.getMessage());
            }
        }
    }
}
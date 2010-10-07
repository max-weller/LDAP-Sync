package de.danielweisser.android.ldapsync.client;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import com.unboundid.ldap.sdk.ReadOnlyEntry;

/**
 * Represents a LDAPSyncAdapter contact
 */
public class Contact {
	public static final String FIRSTNAME = "FIRSTNAME";
	public static final String LASTNAME = "LASTNAME";
	public static final String TELEPHONE = "TELEPHONE";
	public static final String MOBILE = "MOBILE";
	public static final String MAIL = "MAIL";
	public static final String PHOTO = "PHOTO";

	private final String mDN;
	private final String mFirstName;
	private final String mLastName;
	private final String mCellPhone;
	private final String mOfficePhone;
	private final String[] mEmails;
	private final byte[] mImage;

	public String getDN() {
		return mDN;
	}

	public String getFirstName() {
		return mFirstName;
	}

	public String getLastName() {
		return mLastName;
	}

	public String getCellPhone() {
		return mCellPhone;
	}

	public String getOfficePhone() {
		return mOfficePhone;
	}

	public String[] getEmails() {
		return mEmails;
	}

	public byte[] getImage() {
		return mImage;
	}

	public Contact(String dn, String firstName, String lastName, String cellPhone, String officePhone, String[] emails, byte[] image) {
		mDN = dn;
		mFirstName = firstName;
		mLastName = lastName;
		mCellPhone = cellPhone;
		mOfficePhone = officePhone;
		mEmails = emails;
		mImage = image;
	}

	/**
	 * Creates and returns an instance of the user from the provided LDAP data.
	 * 
	 * @param user
	 *            The LDAPObject containing user data
	 * @param mB
	 * @return user The new instance of LDAP user created from the LDAP data.
	 */
	public static Contact valueOf(ReadOnlyEntry user, Bundle mB) {
		try {
			final String dn = user.getDN();
			final String firstName = user.hasAttribute(mB.getString(FIRSTNAME)) ? user.getAttributeValue(mB
					.getString(FIRSTNAME)) : null;
			final String lastName = user.hasAttribute(mB.getString(LASTNAME)) ? user.getAttributeValue(mB
					.getString(LASTNAME)) : null;
			if (firstName == null || lastName == null) {
				return null;
			}
			final String officePhone = user.hasAttribute(mB.getString(TELEPHONE)) ? user.getAttributeValue(mB
					.getString(TELEPHONE)) : null;
			final String cellPhone = user.hasAttribute(mB.getString(MOBILE)) ? user.getAttributeValue(mB
					.getString(MOBILE)) : null;
			final String[] emails = user.hasAttribute(mB.getString(MAIL)) ? user.getAttributeValues(mB.getString(MAIL))
					: null;
			byte[] image = null;
			if (user.hasAttribute(mB.getString(PHOTO))) {
				byte[] array = user.getAttributeValueBytes(mB.getString(PHOTO));

				Bitmap myBitmap = BitmapFactory.decodeByteArray(array, 0, array.length);
				if (myBitmap != null) {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
					image = baos.toByteArray();
				}
			}
			return new Contact(dn, firstName, lastName, cellPhone, officePhone, emails, image);
		} catch (final Exception ex) {
			Log.i("User", "Error parsing LDAP user object" + ex.toString());
		}
		return null;
	}
}
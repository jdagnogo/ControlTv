package com.jeff.controltvbyjeff.services;

import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.jeff.controltvbyjeff.ControlTvApplication;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class NFCService {
    private String tagContent = "";

    public void write(Intent intent, NdefMessage ndefMessage) throws IOException, FormatException {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        writeNdefMessage(tag,ndefMessage);
    }

    public String read(Intent intent) {
        Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (parcelables != null && parcelables.length > 0) {
            readTextFromMessage((NdefMessage) parcelables[0]);
            return tagContent;
        } else {
            Toast.makeText(ControlTvApplication.getAppContext(), "No NDEF messages found!", Toast.LENGTH_SHORT).show();
            return " ";
        }

    }

    private void readTextFromMessage(NdefMessage ndefMessage) {

        NdefRecord[] ndefRecords = ndefMessage.getRecords();

        if (ndefRecords != null && ndefRecords.length > 0) {
            for (NdefRecord ndefRecord : ndefRecords) {

                if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {

                    String tagText = getTextFromNdefRecord(ndefRecord);

                    tagContent = tagText;

                } else {
                    Toast.makeText(ControlTvApplication.getAppContext(), "No NDEF records found!", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    public String getTextFromNdefRecord(NdefRecord ndefRecord) {
        String tagContent = null;
        try {
            byte[] payload = ndefRecord.getPayload();
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
            int languageSize = payload[0] & 0063;
            tagContent = new String(payload, languageSize + 1,
                    payload.length - languageSize - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            Log.e("getTextFromNdefRecord", e.getMessage(), e);
        }
        return tagContent;
    }

    public String getTagID(Tag tag) {
        byte[] tagId = tag.getId();
        String id = new String();
        for (int i = 0; i < tagId.length; i++) {
            String x = Integer.toHexString((int) tagId[i] & 0xff);
            if (x.length() == 1) {
                x = '0' + x;
            }
            id = id + (x + ' ');
        }
        return id;
    }

    private void writeNdefMessage(Tag tag, NdefMessage ndefMessage) throws IOException, FormatException {

        if (tag == null) {
            Toast.makeText(ControlTvApplication.getAppContext(), "Tag object cannot be null", Toast.LENGTH_SHORT).show();
            return;
        }

        Ndef ndef = Ndef.get(tag);

        if (ndef == null) {
            // format tag with the ndef format and writes the message.
            formatTag(tag, ndefMessage);
        } else {
            ndef.connect();

            if (!ndef.isWritable()) {
                Toast.makeText(ControlTvApplication.getAppContext(), "Tag is not writable!", Toast.LENGTH_SHORT).show();

                ndef.close();
                return;
            }

            ndef.writeNdefMessage(ndefMessage);
            ndef.close();

            Toast.makeText(ControlTvApplication.getAppContext(), "Tag writen!", Toast.LENGTH_SHORT).show();

        }

    }

    private void formatTag(Tag tag, NdefMessage ndefMessage) throws IOException, FormatException {

        NdefFormatable ndefFormatable = NdefFormatable.get(tag);

        if (ndefFormatable == null) {
            Toast.makeText(ControlTvApplication.getAppContext(), "Tag is not ndef formatable!", Toast.LENGTH_SHORT).show();
            return;
        }

        ndefFormatable.connect();
        ndefFormatable.format(ndefMessage);
        ndefFormatable.close();

        Toast.makeText(ControlTvApplication.getAppContext(), "Tag writen!", Toast.LENGTH_SHORT).show();

    }
}

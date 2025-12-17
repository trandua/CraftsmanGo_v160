package org.apache.james.mime4j.field.address;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.StringReader;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.james.mime4j.field.address.parser.AddressListParser;
import org.apache.james.mime4j.field.address.parser.ParseException;

/* loaded from: classes.dex */
public class AddressList extends AbstractList<Address> implements Serializable {
    private static final long serialVersionUID = 1;
    private final List<? extends Address> addresses;

    public AddressList(List<? extends Address> list, boolean z) {
        if (list != null) {
            this.addresses = !z ? new ArrayList<>(list) : list;
        } else {
            this.addresses = Collections.emptyList();
        }
    }

    public static void main(String[] strArr) throws Exception {
        String readLine = null;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            try {
                System.out.print("> ");
                readLine = bufferedReader.readLine();
            } catch (Exception e) {
                e.printStackTrace();
                Thread.sleep(300L);
            }
            if (readLine.length() != 0 && !readLine.toLowerCase().equals("exit") && !readLine.toLowerCase().equals("quit")) {
                parse(readLine).print();
            }
            System.out.println("Goodbye.");
            return;
        }
    }

    public static AddressList parse(String str) throws ParseException {
        return Builder.getInstance().buildAddressList(new AddressListParser(new StringReader(str)).parseAddressList());
    }

    public MailboxList flatten() {
        boolean z;
        Iterator<? extends Address> it = this.addresses.iterator();
        while (true) {
            if (it.hasNext()) {
                if (!(((Address) it.next()) instanceof Mailbox)) {
                    z = true;
                    break;
                }
            } else {
                z = false;
                break;
            }
        }
        if (!z) {
            return new MailboxList((List<Mailbox>) this.addresses, true);
        }
        ArrayList arrayList = new ArrayList();
        for (Address address : this.addresses) {
            address.addMailboxesTo(arrayList);
        }
        return new MailboxList(arrayList, false);
    }

    @Override // java.util.AbstractList, java.util.List
    public Address get(int i) {
        return (Address) this.addresses.get(i);
    }

    public void print() {
        for (Address address : this.addresses) {
            System.out.println(address.toString());
        }
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public int size() {
        return this.addresses.size();
    }
}

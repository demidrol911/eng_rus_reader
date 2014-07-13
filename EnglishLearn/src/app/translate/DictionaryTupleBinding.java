package app.translate;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;


public class DictionaryTupleBinding extends TupleBinding<Object>{

    @Override
    public Object entryToObject(TupleInput ti) {
        String translation = ti.readString();
        boolean learn = ti.readBoolean();
        return new DictionaryEntry(translation, learn);
    }

    @Override
    public void objectToEntry(Object e, TupleOutput to) {
        DictionaryEntry de = (DictionaryEntry)e;
        to.writeString(de.getTranslation()); 
        to.writeBoolean(de.isLearn());
    }
    
}

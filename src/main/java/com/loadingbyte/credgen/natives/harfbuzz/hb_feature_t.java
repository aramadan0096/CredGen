// Generated by jextract

package com.loadingbyte.credgen.natives.harfbuzz;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;
import java.lang.foreign.*;
import static java.lang.foreign.ValueLayout.*;
/**
 * {@snippet :
 * struct hb_feature_t {
 *     hb_tag_t tag;
 *     uint32_t value;
 *     unsigned int start;
 *     unsigned int end;
 * };
 * }
 */
public class hb_feature_t {

    public static MemoryLayout $LAYOUT() {
        return constants$0.const$5;
    }
    public static VarHandle tag$VH() {
        return constants$0.const$6;
    }
    /**
     * Getter for field:
     * {@snippet :
     * hb_tag_t tag;
     * }
     */
    public static int tag$get(MemorySegment seg) {
        return (int)constants$0.const$6.get(seg);
    }
    /**
     * Setter for field:
     * {@snippet :
     * hb_tag_t tag;
     * }
     */
    public static void tag$set(MemorySegment seg, int x) {
        constants$0.const$6.set(seg, x);
    }
    public static int tag$get(MemorySegment seg, long index) {
        return (int)constants$0.const$6.get(seg.asSlice(index*sizeof()));
    }
    public static void tag$set(MemorySegment seg, long index, int x) {
        constants$0.const$6.set(seg.asSlice(index*sizeof()), x);
    }
    public static VarHandle value$VH() {
        return constants$0.const$7;
    }
    /**
     * Getter for field:
     * {@snippet :
     * uint32_t value;
     * }
     */
    public static int value$get(MemorySegment seg) {
        return (int)constants$0.const$7.get(seg);
    }
    /**
     * Setter for field:
     * {@snippet :
     * uint32_t value;
     * }
     */
    public static void value$set(MemorySegment seg, int x) {
        constants$0.const$7.set(seg, x);
    }
    public static int value$get(MemorySegment seg, long index) {
        return (int)constants$0.const$7.get(seg.asSlice(index*sizeof()));
    }
    public static void value$set(MemorySegment seg, long index, int x) {
        constants$0.const$7.set(seg.asSlice(index*sizeof()), x);
    }
    public static VarHandle start$VH() {
        return constants$0.const$8;
    }
    /**
     * Getter for field:
     * {@snippet :
     * unsigned int start;
     * }
     */
    public static int start$get(MemorySegment seg) {
        return (int)constants$0.const$8.get(seg);
    }
    /**
     * Setter for field:
     * {@snippet :
     * unsigned int start;
     * }
     */
    public static void start$set(MemorySegment seg, int x) {
        constants$0.const$8.set(seg, x);
    }
    public static int start$get(MemorySegment seg, long index) {
        return (int)constants$0.const$8.get(seg.asSlice(index*sizeof()));
    }
    public static void start$set(MemorySegment seg, long index, int x) {
        constants$0.const$8.set(seg.asSlice(index*sizeof()), x);
    }
    public static VarHandle end$VH() {
        return constants$0.const$9;
    }
    /**
     * Getter for field:
     * {@snippet :
     * unsigned int end;
     * }
     */
    public static int end$get(MemorySegment seg) {
        return (int)constants$0.const$9.get(seg);
    }
    /**
     * Setter for field:
     * {@snippet :
     * unsigned int end;
     * }
     */
    public static void end$set(MemorySegment seg, int x) {
        constants$0.const$9.set(seg, x);
    }
    public static int end$get(MemorySegment seg, long index) {
        return (int)constants$0.const$9.get(seg.asSlice(index*sizeof()));
    }
    public static void end$set(MemorySegment seg, long index, int x) {
        constants$0.const$9.set(seg.asSlice(index*sizeof()), x);
    }
    public static long sizeof() { return $LAYOUT().byteSize(); }
    public static MemorySegment allocate(SegmentAllocator allocator) { return allocator.allocate($LAYOUT()); }
    public static MemorySegment allocateArray(long len, SegmentAllocator allocator) {
        return allocator.allocate(MemoryLayout.sequenceLayout(len, $LAYOUT()));
    }
    public static MemorySegment ofAddress(MemorySegment addr, Arena arena) { return RuntimeHelper.asArray(addr, $LAYOUT(), 1, arena); }
}



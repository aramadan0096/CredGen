// Generated by jextract

package com.loadingbyte.credgen.natives.harfbuzz;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;
import java.lang.foreign.*;
import static java.lang.foreign.ValueLayout.*;
/**
 * {@snippet :
 * struct hb_glyph_info_t {
 *     hb_codepoint_t codepoint;
 *     hb_mask_t mask;
 *     uint32_t cluster;
 *     hb_var_int_t var1;
 *     hb_var_int_t var2;
 * };
 * }
 */
public class hb_glyph_info_t {

    public static MemoryLayout $LAYOUT() {
        return constants$0.const$25;
    }
    public static VarHandle codepoint$VH() {
        return constants$0.const$26;
    }
    /**
     * Getter for field:
     * {@snippet :
     * hb_codepoint_t codepoint;
     * }
     */
    public static int codepoint$get(MemorySegment seg) {
        return (int)constants$0.const$26.get(seg);
    }
    /**
     * Setter for field:
     * {@snippet :
     * hb_codepoint_t codepoint;
     * }
     */
    public static void codepoint$set(MemorySegment seg, int x) {
        constants$0.const$26.set(seg, x);
    }
    public static int codepoint$get(MemorySegment seg, long index) {
        return (int)constants$0.const$26.get(seg.asSlice(index*sizeof()));
    }
    public static void codepoint$set(MemorySegment seg, long index, int x) {
        constants$0.const$26.set(seg.asSlice(index*sizeof()), x);
    }
    public static VarHandle mask$VH() {
        return constants$0.const$27;
    }
    /**
     * Getter for field:
     * {@snippet :
     * hb_mask_t mask;
     * }
     */
    public static int mask$get(MemorySegment seg) {
        return (int)constants$0.const$27.get(seg);
    }
    /**
     * Setter for field:
     * {@snippet :
     * hb_mask_t mask;
     * }
     */
    public static void mask$set(MemorySegment seg, int x) {
        constants$0.const$27.set(seg, x);
    }
    public static int mask$get(MemorySegment seg, long index) {
        return (int)constants$0.const$27.get(seg.asSlice(index*sizeof()));
    }
    public static void mask$set(MemorySegment seg, long index, int x) {
        constants$0.const$27.set(seg.asSlice(index*sizeof()), x);
    }
    public static VarHandle cluster$VH() {
        return constants$0.const$28;
    }
    /**
     * Getter for field:
     * {@snippet :
     * uint32_t cluster;
     * }
     */
    public static int cluster$get(MemorySegment seg) {
        return (int)constants$0.const$28.get(seg);
    }
    /**
     * Setter for field:
     * {@snippet :
     * uint32_t cluster;
     * }
     */
    public static void cluster$set(MemorySegment seg, int x) {
        constants$0.const$28.set(seg, x);
    }
    public static int cluster$get(MemorySegment seg, long index) {
        return (int)constants$0.const$28.get(seg.asSlice(index*sizeof()));
    }
    public static void cluster$set(MemorySegment seg, long index, int x) {
        constants$0.const$28.set(seg.asSlice(index*sizeof()), x);
    }
    public static MemorySegment var1$slice(MemorySegment seg) {
        return seg.asSlice(12, 4);
    }
    public static MemorySegment var2$slice(MemorySegment seg) {
        return seg.asSlice(16, 4);
    }
    public static long sizeof() { return $LAYOUT().byteSize(); }
    public static MemorySegment allocate(SegmentAllocator allocator) { return allocator.allocate($LAYOUT()); }
    public static MemorySegment allocateArray(long len, SegmentAllocator allocator) {
        return allocator.allocate(MemoryLayout.sequenceLayout(len, $LAYOUT()));
    }
    public static MemorySegment ofAddress(MemorySegment addr, Arena arena) { return RuntimeHelper.asArray(addr, $LAYOUT(), 1, arena); }
}



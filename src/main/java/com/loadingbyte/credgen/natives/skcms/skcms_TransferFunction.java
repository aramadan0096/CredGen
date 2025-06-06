// Generated by jextract

package com.loadingbyte.credgen.natives.skcms;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;
import java.lang.foreign.*;
import static java.lang.foreign.ValueLayout.*;
/**
 * {@snippet :
 * struct skcms_TransferFunction {
 *     float g;
 *     float a;
 *     float b;
 *     float c;
 *     float d;
 *     float e;
 *     float f;
 * };
 * }
 */
public class skcms_TransferFunction {

    public static MemoryLayout $LAYOUT() {
        return constants$0.const$97;
    }
    public static VarHandle g$VH() {
        return constants$0.const$98;
    }
    /**
     * Getter for field:
     * {@snippet :
     * float g;
     * }
     */
    public static float g$get(MemorySegment seg) {
        return (float)constants$0.const$98.get(seg);
    }
    /**
     * Setter for field:
     * {@snippet :
     * float g;
     * }
     */
    public static void g$set(MemorySegment seg, float x) {
        constants$0.const$98.set(seg, x);
    }
    public static float g$get(MemorySegment seg, long index) {
        return (float)constants$0.const$98.get(seg.asSlice(index*sizeof()));
    }
    public static void g$set(MemorySegment seg, long index, float x) {
        constants$0.const$98.set(seg.asSlice(index*sizeof()), x);
    }
    public static VarHandle a$VH() {
        return constants$0.const$99;
    }
    /**
     * Getter for field:
     * {@snippet :
     * float a;
     * }
     */
    public static float a$get(MemorySegment seg) {
        return (float)constants$0.const$99.get(seg);
    }
    /**
     * Setter for field:
     * {@snippet :
     * float a;
     * }
     */
    public static void a$set(MemorySegment seg, float x) {
        constants$0.const$99.set(seg, x);
    }
    public static float a$get(MemorySegment seg, long index) {
        return (float)constants$0.const$99.get(seg.asSlice(index*sizeof()));
    }
    public static void a$set(MemorySegment seg, long index, float x) {
        constants$0.const$99.set(seg.asSlice(index*sizeof()), x);
    }
    public static VarHandle b$VH() {
        return constants$0.const$100;
    }
    /**
     * Getter for field:
     * {@snippet :
     * float b;
     * }
     */
    public static float b$get(MemorySegment seg) {
        return (float)constants$0.const$100.get(seg);
    }
    /**
     * Setter for field:
     * {@snippet :
     * float b;
     * }
     */
    public static void b$set(MemorySegment seg, float x) {
        constants$0.const$100.set(seg, x);
    }
    public static float b$get(MemorySegment seg, long index) {
        return (float)constants$0.const$100.get(seg.asSlice(index*sizeof()));
    }
    public static void b$set(MemorySegment seg, long index, float x) {
        constants$0.const$100.set(seg.asSlice(index*sizeof()), x);
    }
    public static VarHandle c$VH() {
        return constants$0.const$101;
    }
    /**
     * Getter for field:
     * {@snippet :
     * float c;
     * }
     */
    public static float c$get(MemorySegment seg) {
        return (float)constants$0.const$101.get(seg);
    }
    /**
     * Setter for field:
     * {@snippet :
     * float c;
     * }
     */
    public static void c$set(MemorySegment seg, float x) {
        constants$0.const$101.set(seg, x);
    }
    public static float c$get(MemorySegment seg, long index) {
        return (float)constants$0.const$101.get(seg.asSlice(index*sizeof()));
    }
    public static void c$set(MemorySegment seg, long index, float x) {
        constants$0.const$101.set(seg.asSlice(index*sizeof()), x);
    }
    public static VarHandle d$VH() {
        return constants$0.const$102;
    }
    /**
     * Getter for field:
     * {@snippet :
     * float d;
     * }
     */
    public static float d$get(MemorySegment seg) {
        return (float)constants$0.const$102.get(seg);
    }
    /**
     * Setter for field:
     * {@snippet :
     * float d;
     * }
     */
    public static void d$set(MemorySegment seg, float x) {
        constants$0.const$102.set(seg, x);
    }
    public static float d$get(MemorySegment seg, long index) {
        return (float)constants$0.const$102.get(seg.asSlice(index*sizeof()));
    }
    public static void d$set(MemorySegment seg, long index, float x) {
        constants$0.const$102.set(seg.asSlice(index*sizeof()), x);
    }
    public static VarHandle e$VH() {
        return constants$0.const$103;
    }
    /**
     * Getter for field:
     * {@snippet :
     * float e;
     * }
     */
    public static float e$get(MemorySegment seg) {
        return (float)constants$0.const$103.get(seg);
    }
    /**
     * Setter for field:
     * {@snippet :
     * float e;
     * }
     */
    public static void e$set(MemorySegment seg, float x) {
        constants$0.const$103.set(seg, x);
    }
    public static float e$get(MemorySegment seg, long index) {
        return (float)constants$0.const$103.get(seg.asSlice(index*sizeof()));
    }
    public static void e$set(MemorySegment seg, long index, float x) {
        constants$0.const$103.set(seg.asSlice(index*sizeof()), x);
    }
    public static VarHandle f$VH() {
        return constants$0.const$104;
    }
    /**
     * Getter for field:
     * {@snippet :
     * float f;
     * }
     */
    public static float f$get(MemorySegment seg) {
        return (float)constants$0.const$104.get(seg);
    }
    /**
     * Setter for field:
     * {@snippet :
     * float f;
     * }
     */
    public static void f$set(MemorySegment seg, float x) {
        constants$0.const$104.set(seg, x);
    }
    public static float f$get(MemorySegment seg, long index) {
        return (float)constants$0.const$104.get(seg.asSlice(index*sizeof()));
    }
    public static void f$set(MemorySegment seg, long index, float x) {
        constants$0.const$104.set(seg.asSlice(index*sizeof()), x);
    }
    public static long sizeof() { return $LAYOUT().byteSize(); }
    public static MemorySegment allocate(SegmentAllocator allocator) { return allocator.allocate($LAYOUT()); }
    public static MemorySegment allocateArray(long len, SegmentAllocator allocator) {
        return allocator.allocate(MemoryLayout.sequenceLayout(len, $LAYOUT()));
    }
    public static MemorySegment ofAddress(MemorySegment addr, Arena arena) { return RuntimeHelper.asArray(addr, $LAYOUT(), 1, arena); }
}



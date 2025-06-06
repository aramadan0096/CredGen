// Generated by jextract

package com.loadingbyte.credgen.natives.decklinkcapi;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;
import java.lang.foreign.*;
import static java.lang.foreign.ValueLayout.*;
final class constants$0 {

    // Suppresses default constructor, ensuring non-instantiability.
    private constants$0() {}
    static final FunctionDescriptor const$0 = FunctionDescriptor.ofVoid(
        RuntimeHelper.POINTER
    );
    static final MethodHandle const$1 = RuntimeHelper.upcallHandle(deviceNotificationCallback_t.class, "apply", constants$0.const$0);
    static final MethodHandle const$2 = RuntimeHelper.downcallHandle(
        constants$0.const$0
    );
    static final FunctionDescriptor const$3 = FunctionDescriptor.ofVoid(
        RuntimeHelper.POINTER,
        JAVA_INT
    );
    static final MethodHandle const$4 = RuntimeHelper.upcallHandle(scheduledFrameCompletionCallback_t.class, "apply", constants$0.const$3);
    static final MethodHandle const$5 = RuntimeHelper.downcallHandle(
        constants$0.const$3
    );
    static final FunctionDescriptor const$6 = FunctionDescriptor.of(JAVA_INT);
    static final MethodHandle const$7 = RuntimeHelper.downcallHandle(
        "PixelFormat_8BitBGRA",
        constants$0.const$6
    );
    static final MethodHandle const$8 = RuntimeHelper.downcallHandle(
        "PixelFormat_10BitRGB",
        constants$0.const$6
    );
    static final MethodHandle const$9 = RuntimeHelper.downcallHandle(
        "FieldDominance_LowerFieldFirst",
        constants$0.const$6
    );
    static final MethodHandle const$10 = RuntimeHelper.downcallHandle(
        "FieldDominance_UpperFieldFirst",
        constants$0.const$6
    );
    static final MethodHandle const$11 = RuntimeHelper.downcallHandle(
        "FieldDominance_ProgressiveFrame",
        constants$0.const$6
    );
    static final MethodHandle const$12 = RuntimeHelper.downcallHandle(
        "FieldDominance_ProgressiveSegmentedFrame",
        constants$0.const$6
    );
    static final MethodHandle const$13 = RuntimeHelper.downcallHandle(
        "DisplayModeFlag_ColorspaceRec601",
        constants$0.const$6
    );
    static final MethodHandle const$14 = RuntimeHelper.downcallHandle(
        "DisplayModeFlag_ColorspaceRec709",
        constants$0.const$6
    );
    static final MethodHandle const$15 = RuntimeHelper.downcallHandle(
        "DisplayModeFlag_ColorspaceRec2020",
        constants$0.const$6
    );
    static final MethodHandle const$16 = RuntimeHelper.downcallHandle(
        "Colorspace_Rec601",
        constants$0.const$6
    );
    static final MethodHandle const$17 = RuntimeHelper.downcallHandle(
        "Colorspace_Rec709",
        constants$0.const$6
    );
    static final MethodHandle const$18 = RuntimeHelper.downcallHandle(
        "Colorspace_Rec2020",
        constants$0.const$6
    );
    static final FunctionDescriptor const$19 = FunctionDescriptor.of(JAVA_BOOLEAN);
    static final MethodHandle const$20 = RuntimeHelper.downcallHandle(
        "initDeckLinkAPI",
        constants$0.const$19
    );
    static final FunctionDescriptor const$21 = FunctionDescriptor.of(RuntimeHelper.POINTER,
        RuntimeHelper.POINTER,
        RuntimeHelper.POINTER
    );
    static final MethodHandle const$22 = RuntimeHelper.downcallHandle(
        "IDeckLinkDeviceNotificationCallback_Create",
        constants$0.const$21
    );
    static final FunctionDescriptor const$23 = FunctionDescriptor.of(RuntimeHelper.POINTER,
        RuntimeHelper.POINTER
    );
    static final MethodHandle const$24 = RuntimeHelper.downcallHandle(
        "IDeckLinkVideoOutputCallback_Create",
        constants$0.const$23
    );
    static final FunctionDescriptor const$25 = FunctionDescriptor.of(RuntimeHelper.POINTER,
        JAVA_INT,
        JAVA_INT,
        JAVA_INT,
        JAVA_INT,
        JAVA_INT,
        JAVA_DOUBLE,
        JAVA_DOUBLE,
        JAVA_DOUBLE,
        JAVA_DOUBLE,
        JAVA_DOUBLE,
        JAVA_DOUBLE,
        JAVA_DOUBLE,
        JAVA_DOUBLE,
        JAVA_DOUBLE,
        JAVA_DOUBLE,
        JAVA_DOUBLE,
        JAVA_DOUBLE,
        JAVA_INT,
        RuntimeHelper.POINTER
    );
    static final MethodHandle const$26 = RuntimeHelper.downcallHandle(
        "IDeckLinkVideoFrame_Create",
        constants$0.const$25
    );
    static final MethodHandle const$27 = RuntimeHelper.downcallHandle(
        "IUnknown_AddRef",
        constants$0.const$0
    );
    static final MethodHandle const$28 = RuntimeHelper.downcallHandle(
        "IUnknown_Release",
        constants$0.const$0
    );
    static final FunctionDescriptor const$29 = FunctionDescriptor.of(RuntimeHelper.POINTER);
    static final MethodHandle const$30 = RuntimeHelper.downcallHandle(
        "IDeckLinkDiscovery_Create",
        constants$0.const$29
    );
    static final FunctionDescriptor const$31 = FunctionDescriptor.of(JAVA_BOOLEAN,
        RuntimeHelper.POINTER,
        RuntimeHelper.POINTER
    );
    static final MethodHandle const$32 = RuntimeHelper.downcallHandle(
        "IDeckLinkDiscovery_InstallDeviceNotifications",
        constants$0.const$31
    );
    static final FunctionDescriptor const$33 = FunctionDescriptor.of(JAVA_BOOLEAN,
        RuntimeHelper.POINTER,
        RuntimeHelper.POINTER,
        JAVA_LONG
    );
    static final MethodHandle const$34 = RuntimeHelper.downcallHandle(
        "IDeckLink_GetDisplayName",
        constants$0.const$33
    );
    static final MethodHandle const$35 = RuntimeHelper.downcallHandle(
        "IDeckLink_QueryIDeckLinkProfileAttributes",
        constants$0.const$23
    );
    static final MethodHandle const$36 = RuntimeHelper.downcallHandle(
        "IDeckLink_QueryIDeckLinkOutput",
        constants$0.const$23
    );
    static final MethodHandle const$37 = RuntimeHelper.downcallHandle(
        "IDeckLinkProfileAttributes_GetDeviceHandle",
        constants$0.const$33
    );
    static final FunctionDescriptor const$38 = FunctionDescriptor.of(JAVA_BOOLEAN,
        RuntimeHelper.POINTER
    );
    static final MethodHandle const$39 = RuntimeHelper.downcallHandle(
        "IDeckLinkProfileAttributes_IsActive",
        constants$0.const$38
    );
    static final MethodHandle const$40 = RuntimeHelper.downcallHandle(
        "IDeckLinkProfileAttributes_SupportsPlayback",
        constants$0.const$38
    );
    static final MethodHandle const$41 = RuntimeHelper.downcallHandle(
        "IDeckLinkOutput_GetDisplayModeIterator",
        constants$0.const$23
    );
    static final FunctionDescriptor const$42 = FunctionDescriptor.of(JAVA_BOOLEAN,
        RuntimeHelper.POINTER,
        JAVA_INT,
        JAVA_INT
    );
    static final MethodHandle const$43 = RuntimeHelper.downcallHandle(
        "IDeckLinkOutput_DoesSupportVideoMode",
        constants$0.const$42
    );
    static final FunctionDescriptor const$44 = FunctionDescriptor.of(JAVA_BOOLEAN,
        RuntimeHelper.POINTER,
        JAVA_INT
    );
    static final MethodHandle const$45 = RuntimeHelper.downcallHandle(
        "IDeckLinkOutput_EnableVideoOutput",
        constants$0.const$44
    );
    static final MethodHandle const$46 = RuntimeHelper.downcallHandle(
        "IDeckLinkOutput_DisableVideoOutput",
        constants$0.const$38
    );
    static final FunctionDescriptor const$47 = FunctionDescriptor.of(JAVA_BOOLEAN,
        RuntimeHelper.POINTER,
        JAVA_LONG,
        JAVA_LONG,
        JAVA_DOUBLE
    );
    static final MethodHandle const$48 = RuntimeHelper.downcallHandle(
        "IDeckLinkOutput_StartScheduledPlayback",
        constants$0.const$47
    );
    static final FunctionDescriptor const$49 = FunctionDescriptor.of(JAVA_BOOLEAN,
        RuntimeHelper.POINTER,
        JAVA_LONG,
        JAVA_LONG
    );
    static final MethodHandle const$50 = RuntimeHelper.downcallHandle(
        "IDeckLinkOutput_StopScheduledPlayback",
        constants$0.const$49
    );
    static final MethodHandle const$51 = RuntimeHelper.downcallHandle(
        "IDeckLinkOutput_SetScheduledFrameCompletionCallback",
        constants$0.const$31
    );
    static final MethodHandle const$52 = RuntimeHelper.downcallHandle(
        "IDeckLinkOutput_DisplayVideoFrameSync",
        constants$0.const$31
    );
    static final FunctionDescriptor const$53 = FunctionDescriptor.of(JAVA_BOOLEAN,
        RuntimeHelper.POINTER,
        RuntimeHelper.POINTER,
        JAVA_LONG,
        JAVA_LONG,
        JAVA_LONG
    );
    static final MethodHandle const$54 = RuntimeHelper.downcallHandle(
        "IDeckLinkOutput_ScheduleVideoFrame",
        constants$0.const$53
    );
    static final MethodHandle const$55 = RuntimeHelper.downcallHandle(
        "IDeckLinkDisplayModeIterator_Next",
        constants$0.const$23
    );
    static final MethodHandle const$56 = RuntimeHelper.downcallHandle(
        "IDeckLinkDisplayMode_GetName",
        constants$0.const$33
    );
    static final FunctionDescriptor const$57 = FunctionDescriptor.of(JAVA_INT,
        RuntimeHelper.POINTER
    );
    static final MethodHandle const$58 = RuntimeHelper.downcallHandle(
        "IDeckLinkDisplayMode_GetDisplayMode",
        constants$0.const$57
    );
    static final MethodHandle const$59 = RuntimeHelper.downcallHandle(
        "IDeckLinkDisplayMode_GetWidth",
        constants$0.const$57
    );
    static final MethodHandle const$60 = RuntimeHelper.downcallHandle(
        "IDeckLinkDisplayMode_GetHeight",
        constants$0.const$57
    );
    static final FunctionDescriptor const$61 = FunctionDescriptor.of(JAVA_LONG,
        RuntimeHelper.POINTER
    );
    static final MethodHandle const$62 = RuntimeHelper.downcallHandle(
        "IDeckLinkDisplayMode_GetFrameRate",
        constants$0.const$61
    );
    static final MethodHandle const$63 = RuntimeHelper.downcallHandle(
        "IDeckLinkDisplayMode_GetFieldDominance",
        constants$0.const$57
    );
    static final MethodHandle const$64 = RuntimeHelper.downcallHandle(
        "IDeckLinkDisplayMode_GetFlags",
        constants$0.const$57
    );
}



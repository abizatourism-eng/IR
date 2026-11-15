# IrRemote
1. افتح المجلد في Android Studio ثم Sync.
2. شغّل على هاتف يحتوي IR (التطبيق لا يعمل على المحاكي).
3. أضف الأكواد في app/src/main/assets/codes.json بالشكل:
   {"name":"power","a":ADDRESS,"c":COMMAND}  (بروتوكول NEC، بالأرقام العشرية)
   مصدر الأكواد: Flipper-IRDB / IRDB على GitHub (ملفات NEC: address و command بالـ hex، حوّلها لعشري).
ملاحظة: أكواد LG مضمّنة كمثال فقط؛ أكواد الماركات الجزائرية فارغة لأنها لم تُتحقق بعد.

## البناء على GitHub
ارفع المشروع إلى مستودع ← تبويب Actions ← Build APK ← Artifacts ← IrRemote-debug-apk.

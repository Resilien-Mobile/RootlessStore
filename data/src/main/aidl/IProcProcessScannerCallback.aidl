import com.baidaidai.rootless_store.data.process.shizuku.dto.ProcCmdlineDto;
import java.util.List;

oneway interface IProcProcessScannerCallback {

    void onResult(in List<ProcCmdlineDto> processList);

}

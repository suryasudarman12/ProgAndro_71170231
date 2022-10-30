class Ovo(nama: String, saldo: Long, noHp: String): MobileWallet(nama, saldo, noHp) {
    val ovoFeeTransferBank : Int = 2000

    override fun transfer(dp: DigitalPayment, nominal: Long) {
        feeTransferBank = 2000
        if(dp is Dana){
            print("OVO tidak valid ")
        }else{
            println("Transfer berhasil ${printBuktiTransfer(penerima = dp, nominal = nominal )}")
        }
    }
}
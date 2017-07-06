package nhs.genetics.cardiff.framework.hgmd;

import org.jsoup.Jsoup;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * client for HGMD webscraping

 */
public class HGMDClientTest {
    private static final String html = "<!DOCTYPE html><html><head><title>HGMD search result</title><link rel='stylesheet' href='css/global.css' type='text/css'><META HTTP-EQUIV='Content-Type' CONTENT='text/html; charset=UTF-8'></head><body><script src=\"analyticstracking.js\"></script>\n" +
            "\n" +
            "<div class='sidemenu' id='opac'>\n" +
            "<div class='sidelink'><span class='a'>\n" +
            "<a class='sidebar' href='search_gene.php'>G</a><br>\n" +
            "<a class='sidebar' href='search_mut.php'>M</a><br>\n" +
            "<a class='sidebar' href='search_phen.php'>P</a><br>\n" +
            "<a class='sidebar' href='search_ref.php'>R</a><br>\n" +
            "<a class='sidebar' href='search_batch.php'>B</a><br><a class='sidebar' href='http://portal.biobase-international.com:80/advanced/begin.html' target='advancedMain' onclick=\"trackOutboundLink('http://portal.biobase-international.com:80/advanced/begin.html'); return false;\">A</a><br><p class='hr'> </p><a class='sidebar' href='stats.php'>S</a><br>\n" +
            "<a class='sidebar' href='info.php'>I</a><br>\n" +
            "<a class='sidebar' href='http://www.qiagenbioinformatics.com/products/human-gene-mutation-database/' target='Support' onclick=\"trackOutboundLink('http://www.qiagenbioinformatics.com/products/human-gene-mutation-database/'); return false;\">S</a><br><p class='hr'> </p><a class='sidebar' href='start.php'>H</a><br><a class='sidebar' href='/cgi-bin/portal/logout.cgi'>L</a><br></span><span class='b'>\n" +
            "<a class='sidebar' href='search_gene.php'>Gene</a><br>\n" +
            "<a class='sidebar' href='search_mut.php'>Mutation</a><br>\n" +
            "<a class='sidebar' href='search_phen.php'>Phenotype</a><br>\n" +
            "<a class='sidebar' href='search_ref.php'>Reference</a><br>\n" +
            "<a class='sidebar' href='search_batch.php'>Batch</a><br><a class='sidebar' href='http://portal.biobase-international.com:80/advanced/begin.html' target='advancedMain' onclick=\"trackOutboundLink('http://portal.biobase-international.com:80/advanced/begin.html'); return false;\">Advanced</a><br><p class='hr'> </p><a class='sidebar' href='stats.php'>Statistics</a><br>\n" +
            "<a class='sidebar' href='info.php'>Information</a><br>\n" +
            "<a class='sidebar' href='http://www.qiagenbioinformatics.com/products/human-gene-mutation-database/' target='Support' onclick=\"trackOutboundLink('http://www.qiagenbioinformatics.com/products/human-gene-mutation-database/'); return false;\">Support</a><br><p class='hr'> </p><a class='sidebar' href='start.php'>Home</a><br><a class='sidebar' href='/cgi-bin/portal/logout.cgi'>Logout</a><br>\n" +
            "</span>\n" +
            "</div>\n" +
            "</div><div class='top'><div class='logo1'><a href='http://www.hgmd.cf.ac.uk/' target='HGMDmain' onclick=\"trackOutboundLink('http://www.hgmd.cf.ac.uk/'); return false;\"><img id='hgmdlogo' src='bar/hgmd_new2.png' alt='HGMD'></a></div><div class='title'>HGMD&reg; Professional 2017.1</div><div class='logo2'><a href='https://www.qiagenbioinformatics.com/' target='QIAGEN' onclick=\"trackOutboundLink('https://www.qiagenbioinformatics.com/'); return false;\"><img src='bar/Qiagen_logo2.png' alt='QIAGEN'></a></div><div class='links'> <a class='sidebar' href='search_gene.php'>Gene</a>\n" +
            "<a class='sidebar' href='search_mut.php'>Mutation</a>\n" +
            "<a class='sidebar' href='search_phen.php'>Phenotype</a>\n" +
            "<a class='sidebar' href='search_ref.php'>Reference</a>\n" +
            "<a class='sidebar' href='search_batch.php'>Batch</a> <a class='sidebar' href='http://portal.biobase-international.com:80/advanced/begin.html' target='advancedMain' onclick=\"trackOutboundLink('http://portal.biobase-international.com:80/advanced/begin.html'); return false;\">Advanced</a> | <a class='sidebar' href='stats.php'>Statistics</a>\n" +
            "<a class='sidebar' href='info.php'>Information</a>\n" +
            "<a class='sidebar' href='http://www.qiagenbioinformatics.com/products/human-gene-mutation-database/' target='Support' onclick=\"trackOutboundLink('http://www.qiagenbioinformatics.com/products/human-gene-mutation-database/'); return false;\">Support</a> |\n" +
            "<a class='sidebar' href='start.php'>Home</a> <a class='sidebar' href='/cgi-bin/portal/logout.cgi'>Logout</a><br></div></div><div class='content'><h3 class='heading2'>batch search result for 1 terms using <span class='green'><i>hg19 VCF</i></span> (DM, DM?, DP, DFP or FP)</h3><div style='text-align:center;'><span style='float:center;'>page 1</span></div><table class='scrolltop'>\n" +
            "<tr style='word-wrap:break-word;'><th class='result' style='width:2.5%;'>#</th><th class='result' style='width:17.5%;'>Search term</th><th class='result' style='width:10%;'>Gene symbol</th><th class='result' style='width:12.5%;'>HGMD mutation</th><th class='result' style='width:15%;'>HGVS</th>\n" +
            "<th class='result' style='width:10%;'>hg38 coordinate</th><th class='result' style='width:7.5%;'>Variant class</th><th class='result' style='width:12.5%;'>dbSNP identifier</th><th class='result' style='width:12.5%;'>HGMD accession</th></tr></table><div class='scrolling'><table class='scrolling'><tr class='odd' style='word-wrap:break-word;'><td style='width:2.5%;'>1</td><td style='width:17.5%;'>2 675623 IDH1 T C</td><td class='center' style='width:10%;'><a href='gene.php?gene=TMEM18'>TMEM18</a></td>\n" +
            "<td class='center' style='width:12.5%;'>Asp22Gly</td><td style='width:15%;'>c.65A>G</td><td style='width:10%;'>chr2:675623</td><td class='center' style='width:7.5%;'><span class='dmq' title='Disease causing mutation ?'>DM?</span></td>\n" +
            "<td style='width:12.5%;'>N/A</td><td class='center' style='width:11.5%;'><form action='mut.php' method='GET'><input type='hidden' name='acc' value='CM1310957'><input type='submit' value='CM1310957'></form></td></tr>\n" +
            "</table></div><p><hr><div class='bott2'>\n" +
            "Designed by P.D.Stenson HGMD&reg;<br><a class='black' href='copyright.php'>Copyright &copy; Cardiff University</a> 2017\n" +
            "</div>\n" +
            "<p class='spacer'></div></body></html>\n";

    @Test
    public void parseDocument() throws Exception {
        assertEquals(HGMDClient.parseDocument(Jsoup.parse(html)).get(0), new HGMDBatchSearchResult(
                1,"2 675623 IDH1 T C", "TMEM18", "Asp22Gly", "c.65A>G", "chr2:675623", HGMDVariantClass.DISEASE_CAUSING_MUTATION_QUERY, null, "CM1310957"
        ));
    }

}